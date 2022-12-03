export interface ICustomer {
  id?: string;
  customerName?: string | null;
  customerLegalEntity?: string | null;
  customerPassword?: string | null;
  customerHashCode?: string | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: string,
    public customerName?: string | null,
    public customerLegalEntity?: string | null,
    public customerPassword?: string | null,
    public customerHashCode?: string | null
  ) {}
}

export function getCustomerIdentifier(customer: ICustomer): string | undefined {
  return customer.id;
}
