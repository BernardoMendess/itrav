import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { TripService } from '../core/trip.service';
import { ActivityService } from '../core/activity.service';
import { ItineraryService } from '../core/itinerary.service';
import { Activity, Itinerary, Trip } from '../core/models';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.css'
})
export class DashboardPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly tripService = inject(TripService);
  private readonly itineraryService = inject(ItineraryService);
  private readonly activityService = inject(ActivityService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly creating = signal(false);
  readonly itineraryLoading = signal(false);
  readonly activityLoading = signal(false);
  readonly errorMessage = signal('');
  readonly trips = signal<Trip[]>([]);
  readonly selectedTripId = signal<number | null>(null);
  readonly selectedItinerary = signal<Itinerary | null>(null);
  readonly activities = signal<Activity[]>([]);

  readonly user = this.authService.user;

  readonly tripForm = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    destination: ['', [Validators.required]],
    startDate: ['', [Validators.required]],
    endDate: ['', [Validators.required]],
    objective: ['lazer', [Validators.required]],
    budget: [0]
  });

  readonly activityForm = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(2)]],
    dayNumber: [1, [Validators.required]]
  });

  ngOnInit(): void {
    this.authService.loadCurrentUser().subscribe({
      error: () => this.logout()
    });
    this.loadTrips();
  }

  loadTrips(): void {
    this.loading.set(true);
    this.tripService.listTrips().subscribe({
      next: (trips) => {
        this.loading.set(false);
        this.trips.set(trips);
      },
      error: (error) => {
        this.loading.set(false);
        this.errorMessage.set(error?.error?.message ?? 'Falha ao carregar viagens');
      }
    });
  }

  createTrip(): void {
    if (this.tripForm.invalid || this.creating()) {
      this.tripForm.markAllAsTouched();
      return;
    }

    this.creating.set(true);
    this.errorMessage.set('');

    const formValue = this.tripForm.getRawValue();
    this.tripService
      .createTrip({
        title: formValue.title,
        destination: formValue.destination,
        startDate: formValue.startDate,
        endDate: formValue.endDate,
        objective: formValue.objective,
        budget: formValue.budget ?? 0,
        status: 'PLANNING'
      })
      .subscribe({
        next: (trip) => {
          this.creating.set(false);
          this.trips.set([trip, ...this.trips()]);
          this.tripForm.reset({
            title: '',
            destination: '',
            startDate: '',
            endDate: '',
            objective: 'lazer',
            budget: 0
          });
        },
        error: (error) => {
          this.creating.set(false);
          this.errorMessage.set(error?.error?.message ?? 'Falha ao criar viagem');
        }
      });
  }

  deleteTrip(id?: number): void {
    if (!id) {
      return;
    }

    this.tripService.deleteTrip(id).subscribe({
      next: () => {
        this.trips.set(this.trips().filter((trip) => trip.id !== id));
      },
      error: (error) => {
        this.errorMessage.set(error?.error?.message ?? 'Falha ao remover viagem');
      }
    });
  }

  openPlanner(tripId?: number): void {
    if (!tripId) {
      return;
    }

    if (this.selectedTripId() === tripId) {
      this.selectedTripId.set(null);
      this.selectedItinerary.set(null);
      this.activities.set([]);
      return;
    }

    this.selectedTripId.set(tripId);
    this.selectedItinerary.set(null);
    this.activities.set([]);
    this.loadLatestItinerary(tripId);
  }

  generateItinerary(): void {
    const tripId = this.selectedTripId();
    if (!tripId) {
      return;
    }

    this.itineraryLoading.set(true);
    this.itineraryService.generateByTrip(tripId).subscribe({
      next: (itinerary) => {
        this.selectedItinerary.set(itinerary);
        this.itineraryLoading.set(false);
        this.loadActivities();
      },
      error: (error) => {
        this.itineraryLoading.set(false);
        this.errorMessage.set(error?.error?.message ?? 'Falha ao gerar itinerário');
      }
    });
  }

  createActivity(): void {
    const itineraryId = this.selectedItinerary()?.id;
    if (!itineraryId || this.activityForm.invalid) {
      this.activityForm.markAllAsTouched();
      return;
    }

    const payload = this.activityForm.getRawValue();
    this.activityService
      .create(itineraryId, {
        title: payload.title,
        dayNumber: payload.dayNumber
      })
      .subscribe({
        next: () => {
          this.activityForm.reset({ title: '', dayNumber: 1 });
          this.loadActivities();
        },
        error: (error) => {
          this.errorMessage.set(error?.error?.message ?? 'Falha ao criar atividade');
        }
      });
  }

  moveActivity(activity: Activity, direction: 'up' | 'down'): void {
    if (!activity.id) {
      return;
    }

    const currentList = this.activities();
    const currentIndex = currentList.findIndex((item) => item.id === activity.id);
    if (currentIndex < 0) {
      return;
    }

    const targetIndex = direction === 'up' ? currentIndex - 1 : currentIndex + 1;
    if (targetIndex < 0 || targetIndex >= currentList.length) {
      return;
    }

    this.activityService.reorder(activity.id, targetIndex).subscribe({
      next: () => this.loadActivities(),
      error: (error) => {
        this.errorMessage.set(error?.error?.message ?? 'Falha ao reordenar atividade');
      }
    });
  }

  deleteActivity(activityId?: number): void {
    if (!activityId) {
      return;
    }

    this.activityService.delete(activityId).subscribe({
      next: () => this.loadActivities(),
      error: (error) => {
        this.errorMessage.set(error?.error?.message ?? 'Falha ao remover atividade');
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  private loadLatestItinerary(tripId: number): void {
    this.itineraryLoading.set(true);
    this.itineraryService.getLatestByTrip(tripId).subscribe({
      next: (itinerary) => {
        this.selectedItinerary.set(itinerary);
        this.itineraryLoading.set(false);
        this.loadActivities();
      },
      error: (error) => {
        this.itineraryLoading.set(false);
        if (error?.status === 404) {
          this.selectedItinerary.set(null);
          this.activities.set([]);
          return;
        }
        this.errorMessage.set(error?.error?.message ?? 'Falha ao obter itinerário');
      }
    });
  }

  private loadActivities(): void {
    const itineraryId = this.selectedItinerary()?.id;
    if (!itineraryId) {
      this.activities.set([]);
      return;
    }

    this.activityLoading.set(true);
    this.activityService.listByItinerary(itineraryId).subscribe({
      next: (activities) => {
        this.activityLoading.set(false);
        this.activities.set(activities);
      },
      error: (error) => {
        this.activityLoading.set(false);
        this.errorMessage.set(error?.error?.message ?? 'Falha ao carregar atividades');
      }
    });
  }
}
