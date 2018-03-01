import { TestBed, inject } from '@angular/core/testing';

import { MinerService } from './miner.service';

describe('MinerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MinerService]
    });
  });

  it('should be created', inject([MinerService], (service: MinerService) => {
    expect(service).toBeTruthy();
  }));
});
