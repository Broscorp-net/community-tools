import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UsersService } from 'src/app/services/users.service';
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  users: User[];
  rowLimit: number;
  daysFetch: number;
  sort: string;

  constructor(private usersService: UsersService, private activatedRoute:ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap
    .subscribe(params => {
      // @ts-ignore
      this.rowLimit = +params.get(`${environment.endpointParamForLeaderboardLimitOfRows}`)||null;
      // @ts-ignore
      this.daysFetch = +params.get(`${environment.endpointParamForLeaderboardPeriodInDays}`)||null;
      // @ts-ignore
      this.sort = +params.get(`${environment.endpointParamForLeaderboardSort}`)||null;
    });

    this.getUsers(this.rowLimit, this.daysFetch, this.sort);
  }

  private getUsers(rowLimit: number, daysFetch: number, sort: string): void {
    let keyName = this.getKeyName(rowLimit, daysFetch, sort);
    let tmp = sessionStorage.getItem(keyName);
    if (tmp == null) {
      this.usersService.getRestUsers(rowLimit, daysFetch, sort).subscribe(
        data => {
          sessionStorage.setItem(keyName, JSON.stringify(data))
          this.users = data;
        });
    } else {
      this.users = JSON.parse(tmp) as User[];
    }
  }

  private getKeyName(rowLimit: number, daysFetch: number, sort: string): string {
    if (daysFetch == null && rowLimit == null && sort == null) {
      return 'leaderboard';
    } else {
      return "leaderboard?" +
        (rowLimit != undefined ? "rowLimit=" + rowLimit + "&" : "") +
        (daysFetch != undefined ? "daysFetch=" + daysFetch + "&" : "") +
        (sort != undefined ? "sort=" + sort : "");
    }
  }

}
