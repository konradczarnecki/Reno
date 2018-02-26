import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Account, KeysDto, Response} from "../model";
import {Observable} from "rxjs/Observable";

@Injectable()
export class AccountService {

  keyfileContent: string;
  keysDto: KeysDto;
  account: Account;

  constructor(private http: HttpClient) { }

  createAccount(): Observable<Response<KeysDto>> {

    const obs = this.http.get<Response<KeysDto>>(environment.apiUrl + '/new-account');
    obs.subscribe(rsp => this.keysDto = rsp.content);

    return obs;
  }

  login(password: string): Observable<Response<Account>> {

    const options = {

      params : new HttpParams()
        .set('keyfileContent', this.keyfileContent)
        .set('password', password)
    };

    const obs = this.http.get<Response<Account>>(environment.apiUrl + '/login-keyfile', options);
    obs.subscribe(rsp => this.account = rsp.content);

    return obs;
  }

  isLogged(): boolean {

    return !!this.keysDto && !!this.account;
  }

  logout() {

    this.account = undefined;
    this.keysDto = undefined;
  }

}
