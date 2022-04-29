import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TabsComponent } from './components/tabs/tabs.component';
import { TaskStatusComponent } from './components/task-status/task-status.component';
import { LeaderboardComponent } from './components/leaderboard/leaderboard.component';

import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { TasksService } from './services/tasks.service';
import { UsersService } from './services/users.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSortModule} from "@angular/material/sort";

const appRoutes: Routes = [
  {path: 'task-status', component: TaskStatusComponent},
  {path: 'leaderboard', component: LeaderboardComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    TabsComponent,
    TaskStatusComponent,
    LeaderboardComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        RouterModule.forRoot(appRoutes),
        BrowserAnimationsModule,
        MatSortModule
    ],
  providers: [TasksService, UsersService],
  bootstrap: [AppComponent]
})
export class AppModule { }
