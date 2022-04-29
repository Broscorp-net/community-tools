import {Component, OnInit} from '@angular/core';
import {User} from 'src/app/models/user.model';
import {UsersService} from 'src/app/services/users.service';
import {TasksService} from 'src/app/services/tasks.service';
import {ActivatedRoute} from "@angular/router";
import {UserTaskStatus} from "../../models/user-task-status.model";
import {MatSortModule, Sort} from '@angular/material/sort';
import {ClipboardService} from "ngx-clipboard";

@Component({
  selector: 'app-task-status',
  templateUrl: './task-status.component.html',
  styleUrls: ['./task-status.component.css']
})
export class TaskStatusComponent implements OnInit {

  tasks: string[];
  users: User[];
  userLimit: number;
  daysFetch: number;


  constructor(private tasksService: TasksService, private usersService: UsersService, private activatedRoute: ActivatedRoute, private clipboardService:ClipboardService) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams
    .subscribe(params => {
      this.userLimit = params.userLimit;
      this.daysFetch = params.daysFetch;
    });
    this.getTasks();
    this.getUsers(this.userLimit, this.daysFetch);
  }

  getTasks(): void {
    this.tasksService.getRestTasks().subscribe(
      data => {
        this.tasks = data;
      });
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
            default:
              return 0;
          }
        });
      }

  isTaskStatusEquals(element: UserTaskStatus, task: string): boolean {
    return element.taskName === task ||
      element.taskName.replace('/', '.') === task;
  }

  getClass(user: User, task: string): any {
    const status =
      user?.taskStatuses?.find(element => this.isTaskStatusEquals(element, task))?.taskStatus;
    return {
      'pull_request': status === 'pull request',
      'done': status === 'done',
      'changes_requested': status === 'changes requested',
      'ready_for_review': status === 'ready for review'
    }
  }

  copyBuff($event : any) : void {
        let text : string = $event.target.getAttribute("title");
        this.clipboardService.copyFromContent(text);
    }
}
