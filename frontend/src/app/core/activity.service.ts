import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Activity, ApiResponse } from './models';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  listByItinerary(itineraryId: number): Observable<Activity[]> {
    return this.http
      .get<ApiResponse<Activity[]>>(`${this.apiUrl}/itineraries/${itineraryId}/activities`)
      .pipe(map((response) => response.data ?? []));
  }

  create(itineraryId: number, payload: Activity): Observable<Activity> {
    return this.http
      .post<ApiResponse<Activity>>(`${this.apiUrl}/itineraries/${itineraryId}/activities`, payload)
      .pipe(map((response) => response.data));
  }

  reorder(activityId: number, orderIndex: number): Observable<Activity> {
    const params = new HttpParams().set('orderIndex', String(orderIndex));
    return this.http
      .patch<ApiResponse<Activity>>(`${this.apiUrl}/activities/${activityId}/reorder`, {}, { params })
      .pipe(map((response) => response.data));
  }

  delete(activityId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<null>>(`${this.apiUrl}/activities/${activityId}`)
      .pipe(map(() => void 0));
  }
}
