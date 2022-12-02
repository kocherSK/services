export interface ICurrencies {
  id?: string;
  currencyName?: string | null;
  currencyCode?: string | null;
}

export class Currencies implements ICurrencies {
  constructor(public id?: string, public currencyName?: string | null, public currencyCode?: string | null) {}
}

export function getCurrenciesIdentifier(currencies: ICurrencies): string | undefined {
  return currencies.id;
}
