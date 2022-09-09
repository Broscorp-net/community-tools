import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {User} from "../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class TasksService {

  private defaultApi: string = `${environment.apiURL}/taskStatus`;

  constructor (private http: HttpClient) {  }

  getTaskNames(): Observable<string[]> {
    return this.http.get<string[]>(this.defaultApi + `/getTasks`);
  }

  // getTaskStatuses(): Observable<User[]> {
  //   return this.http.get<User[]>(this.defaultApi);
  // }

}
