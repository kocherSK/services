import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CurrenciesComponent } from './list/currencies.component';
import { CurrenciesDetailComponent } from './detail/currencies-detail.component';
import { CurrenciesUpdateComponent } from './update/currencies-update.component';
import { CurrenciesDeleteDialogComponent } from './delete/currencies-delete-dialog.component';
import { CurrenciesRoutingModule } from './route/currencies-routing.module';

@NgModule({
  imports: [SharedModule, CurrenciesRoutingModule],
  declarations: [CurrenciesComponent, CurrenciesDetailComponent, CurrenciesUpdateComponent, CurrenciesDeleteDialogComponent],
  entryComponents: [CurrenciesDeleteDialogComponent],
})
export class CurrenciesModule {}
