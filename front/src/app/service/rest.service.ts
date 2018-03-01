import { Injectable } from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {MinerStatus, P2PStatus, Response} from "../model";

@Injectable()
export class RestService {

  constructor(private http: HttpClient) { }

  startMining(miner: string): Observable<Response<any>> {

    const options = {

      params : new HttpParams()
        .set('miner', miner)
    };

    return this.http.get<Response<any>>(environment.apiUrl + '/miner-start', options);
  }

  stopMining(): Observable<Response<any>> {

    return this.http.get<Response<any>>(environment.apiUrl + '/miner-stop');
  }

  minerStatus(): Observable<Response<MinerStatus>> {

    return this.http.get<Response<MinerStatus>>(environment.apiUrl + '/miner-status');
  }

  p2pStatus(): Observable<Response<P2PStatus>> {

    return this.http.get<Response<P2PStatus>>(environment.apiUrl + '/p2p-status');
  }


}
