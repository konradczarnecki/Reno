export interface Response<T> {
  status: string;
  content: T;
}

export interface KeysDto {
  publicKey: string;
  privateKey: string;
}

export interface Account {
  address: string;
  balance: number;
  keys: KeysDto;
}

export interface MinerStatus {
  hashesPerSecond: number;
}

export interface P2PStatus {
  hostCount: number;
  connectedHosts: number;
  inSync: boolean;
  headBlockId: number;
}
