import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICurrencies } from '../currencies.model';
import { CurrenciesService } from '../service/currencies.service';
import { CurrenciesDeleteDialogComponent } from '../delete/currencies-delete-dialog.component';

@Component({
  selector: 'jhi-currencies',
  templateUrl: './currencies.component.html',
})
export class CurrenciesComponent implements OnInit {
  currencies?: ICurrencies[];
  isLoading = false;

  constructor(protected currenciesService: CurrenciesService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.currenciesService.query().subscribe({
      next: (res: HttpResponse<ICurrencies[]>) => {
        this.isLoading = false;
        this.currencies = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ICurrencies): string {
    return item.id!;
  }

  delete(currencies: ICurrencies): void {
    const modalRef = this.modalService.open(CurrenciesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.currencies = currencies;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
