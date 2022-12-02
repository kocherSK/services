import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISmartTrade } from '../smart-trade.model';
import { SmartTradeService } from '../service/smart-trade.service';
import { SmartTradeDeleteDialogComponent } from '../delete/smart-trade-delete-dialog.component';

@Component({
  selector: 'jhi-smart-trade',
  templateUrl: './smart-trade.component.html',
})
export class SmartTradeComponent implements OnInit {
  smartTrades?: ISmartTrade[];
  isLoading = false;

  constructor(protected smartTradeService: SmartTradeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.smartTradeService.query().subscribe({
      next: (res: HttpResponse<ISmartTrade[]>) => {
        this.isLoading = false;
        this.smartTrades = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISmartTrade): string {
    return item.id!;
  }

  delete(smartTrade: ISmartTrade): void {
    const modalRef = this.modalService.open(SmartTradeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.smartTrade = smartTrade;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
