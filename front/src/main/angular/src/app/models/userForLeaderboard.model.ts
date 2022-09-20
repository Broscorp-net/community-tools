export class UserForLeaderboard {

  platformName: string;
  gitName: string;
  dateRegistration: Date;
  dateLastActivity: Date;
  completedTasks: number;
  pointByTask: number;
  karma: number;
  totalPoints: number;

  constructor(platformName: string,
              gitName: string,
              dateRegistration: Date,
              dateLastActivity: Date,
              completedTasks: number,
              totalPoints: number,
              karma: number) {
    this.platformName = platformName;
    this.gitName = gitName;
    this.dateRegistration = dateRegistration;
    this.dateLastActivity = dateLastActivity;
    this.completedTasks = completedTasks;
    this.karma = karma;
    this.totalPoints = totalPoints;
  }

}

