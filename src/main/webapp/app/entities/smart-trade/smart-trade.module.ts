import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SmartTradeComponent } from './list/smart-trade.component';
import { SmartTradeDetailComponent } from './detail/smart-trade-detail.component';
import { SmartTradeUpdateComponent } from './update/smart-trade-update.component';
import { SmartTradeDeleteDialogComponent } from './delete/smart-trade-delete-dialog.component';
import { SmartTradeRoutingModule } from './route/smart-trade-routing.module';

@NgModule({
  imports: [SharedModule, SmartTradeRoutingModule],
  declarations: [SmartTradeComponent, SmartTradeDetailComponent, SmartTradeUpdateComponent, SmartTradeDeleteDialogComponent],
  entryComponents: [SmartTradeDeleteDialogComponent],
})
export class SmartTradeModule {}
