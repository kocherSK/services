import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICurrencies } from '../currencies.model';
import { CurrenciesService } from '../service/currencies.service';

@Component({
  templateUrl: './currencies-delete-dialog.component.html',
})
export class CurrenciesDeleteDialogComponent {
  currencies?: ICurrencies;

  constructor(protected currenciesService: CurrenciesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.currenciesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
