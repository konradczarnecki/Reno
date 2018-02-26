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

export const routes: Routes = [
  { path : '', component : LoginComponent, pathMatch : 'full' },
  { path : 'account', component : AccountComponent },
  { path : 'send', component : SendComponent },
  { path : 'mine', component : MineComponent },
  { path : 'explore', component : ExploreComponent }
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
