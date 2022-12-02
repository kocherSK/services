import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICurrencies, getCurrenciesIdentifier } from '../currencies.model';

export type EntityResponseType = HttpResponse<ICurrencies>;
export type EntityArrayResponseType = HttpResponse<ICurrencies[]>;

@Injectable({ providedIn: 'root' })
export class CurrenciesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/currencies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(currencies: ICurrencies): Observable<EntityResponseType> {
    return this.http.post<ICurrencies>(this.resourceUrl, currencies, { observe: 'response' });
  }

  update(currencies: ICurrencies): Observable<EntityResponseType> {
    return this.http.put<ICurrencies>(`${this.resourceUrl}/${getCurrenciesIdentifier(currencies) as string}`, currencies, {
      observe: 'response',
    });
  }

  partialUpdate(currencies: ICurrencies): Observable<EntityResponseType> {
    return this.http.patch<ICurrencies>(`${this.resourceUrl}/${getCurrenciesIdentifier(currencies) as string}`, currencies, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICurrencies>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICurrencies[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCurrenciesToCollectionIfMissing(
    currenciesCollection: ICurrencies[],
    ...currenciesToCheck: (ICurrencies | null | undefined)[]
  ): ICurrencies[] {
    const currencies: ICurrencies[] = currenciesToCheck.filter(isPresent);
    if (currencies.length > 0) {
      const currenciesCollectionIdentifiers = currenciesCollection.map(currenciesItem => getCurrenciesIdentifier(currenciesItem)!);
      const currenciesToAdd = currencies.filter(currenciesItem => {
        const currenciesIdentifier = getCurrenciesIdentifier(currenciesItem);
        if (currenciesIdentifier == null || currenciesCollectionIdentifiers.includes(currenciesIdentifier)) {
          return false;
        }
        currenciesCollectionIdentifiers.push(currenciesIdentifier);
        return true;
      });
      return [...currenciesToAdd, ...currenciesCollection];
    }
    return currenciesCollection;
  }
}
