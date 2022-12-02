import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SmartTradeComponent } from '../list/smart-trade.component';
import { SmartTradeDetailComponent } from '../detail/smart-trade-detail.component';
import { SmartTradeUpdateComponent } from '../update/smart-trade-update.component';
import { SmartTradeRoutingResolveService } from './smart-trade-routing-resolve.service';

const smartTradeRoute: Routes = [
  {
    path: '',
    component: SmartTradeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SmartTradeDetailComponent,
    resolve: {
      smartTrade: SmartTradeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SmartTradeUpdateComponent,
    resolve: {
      smartTrade: SmartTradeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SmartTradeUpdateComponent,
    resolve: {
      smartTrade: SmartTradeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(smartTradeRoute)],
  exports: [RouterModule],
})
export class SmartTradeRoutingModule {}
