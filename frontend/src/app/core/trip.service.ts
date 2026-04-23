import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, Trip } from './models';

@Injectable({ providedIn: 'root' })
export class TripService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  listTrips(): Observable<Trip[]> {
    return this.http
      .get<ApiResponse<Trip[]>>(`${this.apiUrl}/trips`)
      .pipe(map((response) => response.data ?? []));
  }

  createTrip(payload: Trip): Observable<Trip> {
    return this.http
      .post<ApiResponse<Trip>>(`${this.apiUrl}/trips`, payload)
      .pipe(map((response) => response.data));
  }

  deleteTrip(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<null>>(`${this.apiUrl}/trips/${id}`)
      .pipe(map(() => void 0));
  }
}
