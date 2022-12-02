import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'customer',
        data: { pageTitle: 'gatewaybosApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule),
      },
      {
        path: 'currencies',
        data: { pageTitle: 'gatewaybosApp.currencies.home.title' },
        loadChildren: () => import('./currencies/currencies.module').then(m => m.CurrenciesModule),
      },
      {
        path: 'wallet',
        data: { pageTitle: 'gatewaybosApp.wallet.home.title' },
        loadChildren: () => import('./wallet/wallet.module').then(m => m.WalletModule),
      },
      {
        path: 'smart-trade',
        data: { pageTitle: 'gatewaybosApp.smartTrade.home.title' },
        loadChildren: () => import('./smart-trade/smart-trade.module').then(m => m.SmartTradeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
