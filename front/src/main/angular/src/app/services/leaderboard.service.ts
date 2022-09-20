import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {UserForLeaderboard} from '../models/userForLeaderboard.model';
import {environment} from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private url: string = `${environment.apiURL}${environment.endpointMappingForLeaderboard}`;

  constructor(private http: HttpClient) {}

  getRestUsers(rowLimit: number, daysFetch: number, sort: string): Observable<UserForLeaderboard[]> {
    return this.http.get<UserForLeaderboard[]>
    (this.url, {params: this.getParams(rowLimit, daysFetch, sort)});
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
