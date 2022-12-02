export interface ICustomer {
  id?: string;
  customerLegalEntity?: string | null;
  customerHashCode?: string | null;
}

export class Customer implements ICustomer {
  constructor(public id?: string, public customerLegalEntity?: string | null, public customerHashCode?: string | null) {}
}

export function getCustomerIdentifier(customer: ICustomer): string | undefined {
  return customer.id;
}
