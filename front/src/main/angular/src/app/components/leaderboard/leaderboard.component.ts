import {Component, OnInit} from '@angular/core';
import {UserForLeaderboard} from 'src/app/models/userForLeaderboard.model';
import {LeaderboardService} from 'src/app/services/leaderboard.service';
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../environments/environment";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  usersForLeaderboard: UserForLeaderboard[];
  rowLimit: number;
  daysFetch: number;
  sort: string;

  constructor(private usersService: LeaderboardService,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap
    .subscribe(params => {
      // @ts-ignore
      this.rowLimit = +params.get(`${environment.endpointParamForLimitOfRows}`) || null;
      // @ts-ignore
      this.daysFetch = +params.get(`${environment.endpointParamForPeriodInDays}`) || null;
      // @ts-ignore
      this.sort = +params.get(`${environment.endpointParamForSort}`) || null;
    });

    this.getUsers(this.rowLimit, this.daysFetch, this.sort);
  }

  private getUsers(rowLimit: number, daysFetch: number, sort: string): void {
    let key = this.getKey(`${environment.endpointMappingForLeaderboard}`,
      rowLimit, daysFetch, sort);
    if (this.isStorageContainsValueByKey(key)) {
      // @ts-ignore
      this.usersForLeaderboard = JSON.parse(sessionStorage.getItem(key)) as UserForLeaderboard[];
    } else {
      this.usersService.getRestUsers(rowLimit, daysFetch, sort).subscribe(
        data => {
          this.usersForLeaderboard = data;
          sessionStorage.setItem(key, JSON.stringify(data))
        });
    }
  }

  private isStorageContainsValueByKey(keyName: string): boolean {
    let tmp = sessionStorage.getItem(keyName);
    return tmp != null;
  }

  private getKey(endpoint: string, rowLimit: number, daysFetch: number, sort: string): string {
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
    return endpoint + "?" + queryParams.toString();
  }

}
