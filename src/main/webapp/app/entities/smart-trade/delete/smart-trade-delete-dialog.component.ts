import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISmartTrade } from '../smart-trade.model';
import { SmartTradeService } from '../service/smart-trade.service';

@Component({
  templateUrl: './smart-trade-delete-dialog.component.html',
})
export class SmartTradeDeleteDialogComponent {
  smartTrade?: ISmartTrade;

  constructor(protected smartTradeService: SmartTradeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.smartTradeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
