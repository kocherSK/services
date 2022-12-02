import { ICustomer } from 'app/entities/customer/customer.model';

export interface IWallet {
  id?: string;
  currencyCode?: string | null;
  amount?: number | null;
  customer?: ICustomer | null;
}

export class Wallet implements IWallet {
  constructor(public id?: string, public currencyCode?: string | null, public amount?: number | null, public customer?: ICustomer | null) {}
}

export function getWalletIdentifier(wallet: IWallet): string | undefined {
  return wallet.id;
}
