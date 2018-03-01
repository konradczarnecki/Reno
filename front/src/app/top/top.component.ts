import { Component, OnInit } from '@angular/core';
import {AccountService} from "../service/account.service";

@Component({
  selector: 'app-top',
  templateUrl: './top.component.html',
  styleUrls: ['./top.component.scss']
})
export class TopComponent implements OnInit {

  addressVisible: boolean;

  constructor(private accountService: AccountService) { }

  ngOnInit() {
    this.addressVisible = false;
  }

  get account() {
    return this.accountService.account;
  }

  logout() {
    this.accountService.logout();
  }

}
