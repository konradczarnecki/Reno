import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable()
export class AccountService {

  keyfileContent: string;

  constructor(private http: HttpClient) { }

  createAccount() {

    this.http.get(environment.apiUrl + '/create-account');
  }

}
