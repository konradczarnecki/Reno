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
}
