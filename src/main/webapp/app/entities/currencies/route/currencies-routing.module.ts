import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CurrenciesComponent } from '../list/currencies.component';
import { CurrenciesDetailComponent } from '../detail/currencies-detail.component';
import { CurrenciesUpdateComponent } from '../update/currencies-update.component';
import { CurrenciesRoutingResolveService } from './currencies-routing-resolve.service';

const currenciesRoute: Routes = [
  {
    path: '',
    component: CurrenciesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CurrenciesDetailComponent,
    resolve: {
      currencies: CurrenciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CurrenciesUpdateComponent,
    resolve: {
      currencies: CurrenciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CurrenciesUpdateComponent,
    resolve: {
      currencies: CurrenciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(currenciesRoute)],
  exports: [RouterModule],
})
export class CurrenciesRoutingModule {}
