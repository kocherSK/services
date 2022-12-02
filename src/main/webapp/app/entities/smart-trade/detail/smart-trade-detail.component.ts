import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISmartTrade } from '../smart-trade.model';

@Component({
  selector: 'jhi-smart-trade-detail',
  templateUrl: './smart-trade-detail.component.html',
})
export class SmartTradeDetailComponent implements OnInit {
  smartTrade: ISmartTrade | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ smartTrade }) => {
      this.smartTrade = smartTrade;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
