import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { TopComponent } from './top/top.component';
import { AccountComponent } from './account/account.component';
import { SendComponent } from './send/send.component';
import { MineComponent } from './mine/mine.component';
import { ExploreComponent } from './explore/explore.component';
import { AccountService } from './service/account.service';
import { FetchService } from './service/fetch.service';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material";

export const routes: Routes = [
  { path : '', redirectTo : '/login' , pathMatch : 'full' },
  { path : 'login', component : LoginComponent },
  { path : 'account', component : AccountComponent, canActivate : [AccountService] },
  { path : 'send', component : SendComponent, canActivate : [AccountService] },
  { path : 'mine', component : MineComponent, canActivate : [AccountService] },
  { path : 'explore', component : ExploreComponent, canActivate : [AccountService] }
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    TopComponent,
    AccountComponent,
    SendComponent,
    MineComponent,
    ExploreComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    FormsModule
  ],
  providers: [AccountService, FetchService],
  bootstrap: [AppComponent]
})
export class AppModule { }
