import { UserTaskStatus } from "./user-task-status.model";

export class UserForTaskStatus {

  platformName: string;
  gitName: string;
  dateRegistration: Date;
  dateLastActivity: Date;
  completedTasks: number;
  taskStatuses: UserTaskStatus[];

  constructor(platformName: string,
              gitName: string,
              dateRegistration: Date,
              dateLastActivity: Date,
              completedTasks: number,
              taskStatuses: UserTaskStatus[]) {
    this.platformName = platformName;
    this.gitName = gitName;
    this.dateRegistration = dateRegistration;
    this.dateLastActivity = dateLastActivity;
    this.completedTasks = completedTasks;
    this.taskStatuses = taskStatuses;
  }

}
