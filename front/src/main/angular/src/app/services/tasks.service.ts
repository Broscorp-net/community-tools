import {HttpClient, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {UserForTaskStatus} from "../models/userForTaskStatus.model";

@Injectable({
  providedIn: 'root'
})
export class TasksService {

  private defaultApi: string = `${environment.apiURL}`;

  constructor (private http: HttpClient) {}

  getTaskNames(): Observable<string[]> {
    return this.http.get<string[]>(this.defaultApi + `${environment.endpointMappingForGetTasks}`);
  }

  getTaskStatuses(rowLimit: number, daysFetch: number, sort: string): Observable<UserForTaskStatus[]> {
    return this.http.get<UserForTaskStatus[]>(this.defaultApi + `${environment.endpointMappingForGetTaskStatuses}`,
      {params: this.getParams(rowLimit, daysFetch, sort)});
  }

  private getParams(rowLimit: number | null, daysFetch: number | null, sort: string | null): HttpParams {
    let queryParams = new HttpParams();
    if (daysFetch != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForPeriodInDays}`, daysFetch);
    }
    if (rowLimit != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForLimitOfRows}`, rowLimit);
    }
    if (sort != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForSort}`, sort);
    }
    return queryParams;
  }

}
