import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UsersService } from 'src/app/services/users.service';
import {ActivatedRoute} from "@angular/router";
import {MatSortModule, Sort} from '@angular/material/sort';

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  users: User[];
  userLimit: number;
  daysFetch: number;

  constructor(private usersService: UsersService, private activatedRoute:ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap
    .subscribe(params => {
      // @ts-ignore
      this.userLimit = +params.get('userLimit')||null;
      // @ts-ignore
      this.daysFetch = +params.get('daysFetch')||null;
    });

    this.getUsers(this.userLimit, this.daysFetch);
  }

  getUsers(userLimit: number, daysFetch: number): void {
    this.usersService.getRestUsers(userLimit, daysFetch).subscribe(
      data => {
        this.users = data;
      });

  }

  sortData(sort: Sort) {
    const data = this.users.slice();
    if (!sort.active || sort.direction === '') {
      this.users = data;
      return;
    }

    function compare(a: number | string | Date, b: number | string | Date, isAsc: boolean) {
      return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
    }

    this.users = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'platformName':
          return compare(a.platformName, b.platformName, isAsc);
        case 'GitName':
          return compare(a.gitName, b.gitName, isAsc);
        case 'Tasks':
          return compare(a.completedTasks, b.completedTasks, isAsc);
        case 'Points':
          return compare(a.pointByTask, b.pointByTask, isAsc);
        case 'Karma':
          return compare(a.karma, b.karma, isAsc);
        case 'TotalPoints':
          return compare(a.totalPoints, b.totalPoints, isAsc);
        default:
          return 0;
      }
    });
  }
}
