import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICurrencies } from '../currencies.model';

@Component({
  selector: 'jhi-currencies-detail',
  templateUrl: './currencies-detail.component.html',
})
export class CurrenciesDetailComponent implements OnInit {
  currencies: ICurrencies | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ currencies }) => {
      this.currencies = currencies;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
