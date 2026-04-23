import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, Itinerary } from './models';

@Injectable({ providedIn: 'root' })
export class ItineraryService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  generateByTrip(tripId: number): Observable<Itinerary> {
    return this.http
      .post<ApiResponse<Itinerary>>(`${this.apiUrl}/trips/${tripId}/itineraries/generate`, {})
      .pipe(map((response) => response.data));
  }

  getLatestByTrip(tripId: number): Observable<Itinerary> {
    return this.http
      .get<ApiResponse<Itinerary>>(`${this.apiUrl}/trips/${tripId}/itineraries/latest`)
      .pipe(map((response) => response.data));
  }
}
