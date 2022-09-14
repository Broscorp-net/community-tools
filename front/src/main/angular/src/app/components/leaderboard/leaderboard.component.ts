import {Component, OnInit} from '@angular/core';
import {UserForLeaderboard} from 'src/app/models/userForLeaderboard.model';
import {LeaderboardService} from 'src/app/services/leaderboard.service';
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../environments/environment";
import {UtilService} from "../../services/util.service";

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
    let key = UtilService.getKey(`${environment.endpointMappingForLeaderboard}`,
      rowLimit, daysFetch, sort);
    if (UtilService.isStorageContainsValueByKey(key)) {
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

}
