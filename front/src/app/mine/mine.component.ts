import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {RestService} from "../service/rest.service";
import {MinerStatus} from "../model";
import {environment} from "../../environments/environment";
import {AccountService} from "../service/account.service";
import {MinerService} from "../service/miner.service";
import {HashAnimationComponent} from "./hash-animation/hash-animation.component";

@Component({
  selector: 'app-mine',
  templateUrl: './mine.component.html',
  styleUrls: ['./mine.component.scss']
})
export class MineComponent implements OnInit {

  @ViewChild(HashAnimationComponent) animation: HashAnimationComponent;

  constructor(private rest: RestService, private minerService: MinerService) { }

  ngOnInit() {}

  startMining() {
    this.minerService.startMining();
    this.animation.startAnimation();
  }

  stopMining() {
    this.minerService.stopMining();
  }


  status() {
    return this.minerService.status;
  }

  isMining() {
    return this.minerService.doMine;
  }

  get hashesPerSecondFormatted() {

    let val = this.minerService.status.hashesPerSecond;
    let formatted = '';

    if(val > 1000 && val < 1000000) formatted = (val / 1000).toFixed(0) + ' k';
    else if(val > 1000000) formatted = (val / 1000000).toFixed(2) + ' M';
    else formatted = val + '';

    return formatted;
  }

}
