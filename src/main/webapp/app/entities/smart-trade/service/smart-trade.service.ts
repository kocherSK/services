import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISmartTrade, getSmartTradeIdentifier } from '../smart-trade.model';

export type EntityResponseType = HttpResponse<ISmartTrade>;
export type EntityArrayResponseType = HttpResponse<ISmartTrade[]>;

@Injectable({ providedIn: 'root' })
export class SmartTradeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/smart-trades');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(smartTrade: ISmartTrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smartTrade);
    return this.http
      .post<ISmartTrade>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(smartTrade: ISmartTrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smartTrade);
    return this.http
      .put<ISmartTrade>(`${this.resourceUrl}/${getSmartTradeIdentifier(smartTrade) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(smartTrade: ISmartTrade): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smartTrade);
    return this.http
      .patch<ISmartTrade>(`${this.resourceUrl}/${getSmartTradeIdentifier(smartTrade) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ISmartTrade>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISmartTrade[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSmartTradeToCollectionIfMissing(
    smartTradeCollection: ISmartTrade[],
    ...smartTradesToCheck: (ISmartTrade | null | undefined)[]
  ): ISmartTrade[] {
    const smartTrades: ISmartTrade[] = smartTradesToCheck.filter(isPresent);
    if (smartTrades.length > 0) {
      const smartTradeCollectionIdentifiers = smartTradeCollection.map(smartTradeItem => getSmartTradeIdentifier(smartTradeItem)!);
      const smartTradesToAdd = smartTrades.filter(smartTradeItem => {
        const smartTradeIdentifier = getSmartTradeIdentifier(smartTradeItem);
        if (smartTradeIdentifier == null || smartTradeCollectionIdentifiers.includes(smartTradeIdentifier)) {
          return false;
        }
        smartTradeCollectionIdentifiers.push(smartTradeIdentifier);
        return true;
      });
      return [...smartTradesToAdd, ...smartTradeCollection];
    }
    return smartTradeCollection;
  }

  protected convertDateFromClient(smartTrade: ISmartTrade): ISmartTrade {
    return Object.assign({}, smartTrade, {
      valueDate: smartTrade.valueDate?.isValid() ? smartTrade.valueDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.valueDate = res.body.valueDate ? dayjs(res.body.valueDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((smartTrade: ISmartTrade) => {
        smartTrade.valueDate = smartTrade.valueDate ? dayjs(smartTrade.valueDate) : undefined;
      });
    }
    return res;
  }
}
