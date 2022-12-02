import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICurrencies, Currencies } from '../currencies.model';
import { CurrenciesService } from '../service/currencies.service';

@Injectable({ providedIn: 'root' })
export class CurrenciesRoutingResolveService implements Resolve<ICurrencies> {
  constructor(protected service: CurrenciesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICurrencies> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((currencies: HttpResponse<Currencies>) => {
          if (currencies.body) {
            return of(currencies.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Currencies());
  }
}
