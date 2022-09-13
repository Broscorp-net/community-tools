import {HttpClient} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {UserForTaskStatus} from "../models/userForTaskStatus.model";
import {UtilService} from "./util.service";

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
      {params: UtilService.getParams(rowLimit, daysFetch, sort)});
  }

}
