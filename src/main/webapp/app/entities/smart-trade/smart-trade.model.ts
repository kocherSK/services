import dayjs from 'dayjs/esm';

export interface ISmartTrade {
  id?: string;
  counterParty?: string | null;
  currencyBuy?: string | null;
  currencySell?: string | null;
  rate?: number | null;
  amount?: number | null;
  contraAmount?: number | null;
  valueDate?: dayjs.Dayjs | null;
  transactionId?: string | null;
  direction?: string | null;
}

export class SmartTrade implements ISmartTrade {
  constructor(
    public id?: string,
    public counterParty?: string | null,
    public currencyBuy?: string | null,
    public currencySell?: string | null,
    public rate?: number | null,
    public amount?: number | null,
    public contraAmount?: number | null,
    public valueDate?: dayjs.Dayjs | null,
    public transactionId?: string | null,
    public direction?: string | null
  ) {}
}

export function getSmartTradeIdentifier(smartTrade: ISmartTrade): string | undefined {
  return smartTrade.id;
}
