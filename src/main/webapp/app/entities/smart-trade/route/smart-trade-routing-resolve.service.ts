import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISmartTrade, SmartTrade } from '../smart-trade.model';
import { SmartTradeService } from '../service/smart-trade.service';

@Injectable({ providedIn: 'root' })
export class SmartTradeRoutingResolveService implements Resolve<ISmartTrade> {
  constructor(protected service: SmartTradeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISmartTrade> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((smartTrade: HttpResponse<SmartTrade>) => {
          if (smartTrade.body) {
            return of(smartTrade.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SmartTrade());
  }
}
