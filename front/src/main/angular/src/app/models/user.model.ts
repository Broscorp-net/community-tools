import { UserTaskStatus } from "./user-task-status.model";

export class User {
  gitName: string;
  dateRegistrationForFront: string;
  dateLastActivityForFront: string;
  karma: number;
  pointByTask: number;
  platformName: string;
  completedTasks: number;
  taskStatuses: UserTaskStatus[];
  totalPoints: number;

  constructor(gitName: string, dateRegistrationForFront: string, dateLastActivityForFront: string, karma: number, pointByTask:number, platformName: string, completedTasks: number, taskStatuses: UserTaskStatus[], totalPoints: number) {
    this.gitName = gitName;
    this.dateRegistrationForFront = dateRegistrationForFront;
    this.dateLastActivityForFront = dateLastActivityForFront;
    this.platformName = platformName;
    this.taskStatuses = taskStatuses;
    this.completedTasks = completedTasks;
    this.karma = karma;
    this.totalPoints = totalPoints;
  }

}

