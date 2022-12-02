import { IUser } from 'app/entities/user/user.model';

export interface ICustomer {
  id?: string;
  customerLegalEntity?: string | null;
  customerHashCode?: string | null;
  user?: IUser | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: string,
    public customerLegalEntity?: string | null,
    public customerHashCode?: string | null,
    public user?: IUser | null
  ) {}
}

export function getCustomerIdentifier(customer: ICustomer): string | undefined {
  return customer.id;
}
