import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {UserForLeaderboard} from '../models/userForLeaderboard.model';
import {environment} from 'src/environments/environment';
import {UtilService} from './util.service'

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private url: string = `${environment.apiURL}${environment.endpointMappingForLeaderboard}`;

  constructor(private http: HttpClient) {}

  getRestUsers(rowLimit: number, daysFetch: number, sort: string): Observable<UserForLeaderboard[]> {
    return this.http.get<UserForLeaderboard[]>
    (this.url, {params: UtilService.getParams(rowLimit, daysFetch, sort)});
  }

}
