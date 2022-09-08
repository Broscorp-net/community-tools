import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../models/user.model';
import {environment} from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private defaultApi: string = `${environment.apiURL}/leaderboard`;

  paramFormedString: string;

  constructor(private http: HttpClient) {
  }

  getRestUsers(userLimit: number, daysFetch: number, sort: string): Observable<User[]> {
    if (daysFetch == null && sort == null && userLimit == null) {
      return this.http.get<User[]>(this.defaultApi);
    }
    this.paramFormedString = "?" +
      (userLimit != undefined ? `${environment.endpointNameForLeaderboardLimitOfRows}` + "=" + userLimit + "&" : "") +
      (daysFetch != undefined ? `${environment.endpointNameForLeaderboardPeriodInDays}`+ "=" + daysFetch + "&" : "") +
      (sort != undefined ? `${environment.endpointNameForLeaderboardSort}` + "=" + sort : "");
    return this.http.get<User[]>(this.defaultApi + this.paramFormedString);
  }

}
