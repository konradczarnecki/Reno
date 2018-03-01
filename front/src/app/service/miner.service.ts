import { Injectable } from '@angular/core';
import {RestService} from "./rest.service";
import {MinerStatus} from "../model";
import {AccountService} from "./account.service";

@Injectable()
export class MinerService {

  doMine: boolean;
  status: MinerStatus;
  updateStatusIntervalId: number;

  constructor(private rest: RestService, private accountService: AccountService) {

    this.doMine = false;
    this.status = {
      hashesPerSecond : 0
    }
  }

  startMining() {

    this.rest.startMining(this.accountService.account.address).subscribe(rsp => {

      if(rsp.status == 'success') {

        this.doMine = true;
        this.updateStatusIntervalId = setInterval(this.updateStatus.bind(this), 500);
      }
    })
  }

  stopMining() {

    this.rest.stopMining().subscribe(rsp => {

      if(rsp.status == 'success') {

        this.doMine = false;
        setTimeout(() => clearInterval(this.updateStatusIntervalId), 2000);
      }
    })
  }

  updateStatus() {

    this.rest.minerStatus().subscribe(rsp => {

      if(rsp.status == 'success') this.status = rsp.content;
    })
  }

}
