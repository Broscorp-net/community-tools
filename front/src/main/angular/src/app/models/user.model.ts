import { UserTaskStatus } from "./user-task-status.model";

export class User {
  gitName: string;
  dateOfRegistration: string;
  dateOfLastActivity: string;
  karma: number;
  pointByTask: number;
  platformName: string;
  completedTasks: number;
  taskStatuses: UserTaskStatus[];
  totalPoints: number;

  constructor(gitName: string, dateOfRegistration: string, dateOfLastActivity: string, karma: number, pointByTask:number, platformName: string, completedTasks: number, taskStatuses: UserTaskStatus[], totalPoints: number) {
    this.gitName = gitName;
    this.dateOfRegistration = dateOfRegistration;
    this.dateOfLastActivity = dateOfLastActivity;
    this.platformName = platformName;
    this.taskStatuses = taskStatuses;
    this.completedTasks = completedTasks;
    this.karma = karma;
    this.totalPoints = totalPoints;
  }

}

