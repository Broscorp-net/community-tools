import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../models/user.model';
import {environment} from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private defaultApi: string = `${environment.apiURL}/api/users`;

  paramFormedString: string;

  constructor(private http: HttpClient) {
  }

  getRestUsers(userLimit: number, sort: string): Observable<User[]> {
    this.paramFormedString = "?" +
      (userLimit != undefined ? "userLimit=" + userLimit + "&" : "") +
      (sort != undefined ? "sort=" + sort : "");
    return this.http.get<User[]>(this.defaultApi + this.paramFormedString);
  }

  getRestUsersWithDaysFetch(daysFetch: number): Observable<User[]> {
    this.paramFormedString = "?" +
      (daysFetch != undefined ? "daysFetch=" + daysFetch + "&" : "");
    return this.http.get<User[]>(this.defaultApi + this.paramFormedString);
  }

}
