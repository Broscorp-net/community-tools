import { TestBed } from '@angular/core/testing';

import { LeaderboardService } from './leaderboard.service';

describe('UsersServiceService', () => {
  let service: LeaderboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaderboardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
