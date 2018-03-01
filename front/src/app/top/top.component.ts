import { Component, OnInit } from '@angular/core';
import {AccountService} from "../service/account.service";
import {RestService} from "../service/rest.service";
import {P2PStatus} from "../model";

@Component({
  selector: 'app-top',
  templateUrl: './top.component.html',
  styleUrls: ['./top.component.scss']
})
export class TopComponent implements OnInit {

  addressVisible: boolean;
  p2pStatus: P2PStatus;

  constructor(private accountService: AccountService, private rest: RestService) { }

  ngOnInit() {
    this.addressVisible = false;
    this.getStatus();
    setInterval(this.getStatus.bind(this), 2000);
  }

  get account() {
    return this.accountService.account;
  }

  logout() {
    this.accountService.logout();
  }

  getStatus() {

    this.rest.p2pStatus().subscribe(rsp => {
      if(rsp.status == 'success') this.p2pStatus = rsp.content;
    })
  }

}
