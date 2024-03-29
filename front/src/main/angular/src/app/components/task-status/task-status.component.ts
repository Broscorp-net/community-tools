import {Component, OnInit} from '@angular/core';
import {UserForTaskStatus} from 'src/app/models/userForTaskStatus.model';
import {TasksService} from 'src/app/services/tasks.service';
import {ActivatedRoute} from "@angular/router";
import {UserTaskStatus} from "../../models/user-task-status.model";
import {environment} from "../../../environments/environment";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-task-status',
  templateUrl: './task-status.component.html',
  styleUrls: ['./task-status.component.css']
})
export class TaskStatusComponent implements OnInit {

  tasks: string[];
  userForTaskStatuses: UserForTaskStatus[];

  rowLimit: number;
  daysFetch: number;
  sort: string;

  constructor(private tasksService: TasksService,
              private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.queryParamMap
    .subscribe(params => {
      // @ts-ignore
      this.rowLimit = +params.get(`${environment.endpointParamForLimitOfRows}`)||null;
      // @ts-ignore
      this.daysFetch = +params.get(`${environment.endpointParamForPeriodInDays}`)||null;
      // @ts-ignore
      this.sort = +params.get(`${environment.endpointParamForSort}`)||null;
    });
    this.getTasks();
    this.getUsers(this.rowLimit, this.daysFetch, this.sort);
  }

  getTasks(): void {
    this.tasksService.getTaskNames().subscribe(data => {
      this.tasks = data;
    });
  }

  isTaskStatusEquals(element: UserTaskStatus, task: string): boolean {
    return element.taskName === task ||
      element.taskName.replace('/', '.') === task;
  }

  getClass(user: UserForTaskStatus, task: string): any {
    const status =
      user?.taskStatuses?.find(element => this.isTaskStatusEquals(element, task))?.taskStatus;
    return {
      'pull_request': status === 'pull request',
      'done': status === 'done',
        'changes_requested': status === 'changes requested',
      'ready_for_review': status === 'ready for review',
      'undefined': status === 'undefined'
    }
  }

  private getUsers(rowLimit: number, daysFetch: number, sort: string): void {
    let key = this.getKey(`${environment.endpointMappingForGetTaskStatuses}`,
      rowLimit, daysFetch, sort);
    if (this.isStorageContainsValueByKey(key)) {
      // @ts-ignore
      this.userForTaskStatuses = JSON.parse(sessionStorage.getItem(key)) as UserForTaskStatus[];
    } else {
      this.tasksService.getTaskStatuses(rowLimit, daysFetch, sort).subscribe(
        data => {
          this.userForTaskStatuses = data;
          sessionStorage.setItem(key,  JSON.stringify(data))
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
