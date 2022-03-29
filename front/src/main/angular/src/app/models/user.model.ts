import { UserTaskStatus } from "./user-task-status.model";

export class User {
  gitName: string;
  dateOfRegistrationForFront: string;
  dateOfLastActivity: string;
  karma: number;
  pointByTask: number;
  platformName: string;
  completedTasks: number;
  taskStatuses: UserTaskStatus[];
  totalPoints: number;

  constructor(gitName: string, dateOfRegistrationForFront: string, dateOfLastActivity: string, karma: number, pointByTask:number, platformName: string, completedTasks: number, taskStatuses: UserTaskStatus[], totalPoints: number) {
    this.gitName = gitName;
    this.dateOfRegistrationForFront = dateOfRegistrationForFront;
    this.dateOfLastActivity = dateOfLastActivity;
    this.platformName = platformName;
    this.taskStatuses = taskStatuses;
    this.completedTasks = completedTasks;
    this.karma = karma;
    this.totalPoints = totalPoints;
  }

}

