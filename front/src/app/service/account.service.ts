import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Account, KeysDto, Response} from "../model";
import {Observable} from "rxjs/Observable";
import {CanActivate, Router} from "@angular/router";

@Injectable()
export class AccountService implements CanActivate {

  keyfileContent: string;
  account: Account;

  constructor(private http: HttpClient, private router: Router) {
    this.loadAccount();
  }

  createAccount(): Observable<Response<Account>> {

    const obs = this.http.get<Response<Account>>(environment.apiUrl + '/new-account');
    obs.subscribe(rsp => this.account = rsp.content);

    return obs;
  }

  login(password: string): Observable<Response<Account>> {

    const options = {

      params : new HttpParams()
        .set('keyfileContent', this.keyfileContent)
        .set('password', password)
    };

    const obs = this.http.get<Response<Account>>(environment.apiUrl + '/login-keyfile', options);

    obs.subscribe(rsp => {

      if(rsp.status != 'success') return;

      this.account = rsp.content;
      this.bindAccount();
      this.router.navigate(['/account']);
    });

    return obs;
  }

  isLogged(): boolean {

    return !!this.account;
  }

  logout() {

    this.account = undefined;
    this.router.navigate(['/login']);
  }

  canActivate() {

    if(!this.isLogged()) this.router.navigate(['/login']);
    return this.isLogged();
  }

  bindAccount() {
    localStorage.setItem('account', JSON.stringify(this.account));
  }

  loadAccount() {
    this.account = JSON.parse(localStorage.getItem('account'));
  }
}
