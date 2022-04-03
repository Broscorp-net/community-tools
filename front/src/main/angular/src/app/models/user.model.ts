import { UserTaskStatus } from "./user-task-status.model";

export class User {
  gitName: string;
  dateRegistrationFront: string;
  dateLastActivityFront: string;
  karma: number;
  pointByTask: number;
  platformName: string;
  completedTasks: number;
  taskStatuses: UserTaskStatus[];
  totalPoints: number;

  constructor(gitName: string, dateRegistrationFront: string, dateLastActivityFront: string, karma: number, pointByTask:number, platformName: string, completedTasks: number, taskStatuses: UserTaskStatus[], totalPoints: number) {
    this.gitName = gitName;
    this.dateRegistrationFront = dateRegistrationFront;
    this.dateLastActivityFront = dateLastActivityFront;
    this.platformName = platformName;
    this.taskStatuses = taskStatuses;
    this.completedTasks = completedTasks;
    this.karma = karma;
    this.totalPoints = totalPoints;
  }

}

