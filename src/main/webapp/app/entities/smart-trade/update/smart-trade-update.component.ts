import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISmartTrade, SmartTrade } from '../smart-trade.model';
import { SmartTradeService } from '../service/smart-trade.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

@Component({
  selector: 'jhi-smart-trade-update',
  templateUrl: './smart-trade-update.component.html',
})
export class SmartTradeUpdateComponent implements OnInit {
  isSaving = false;

  customersSharedCollection: ICustomer[] = [];

  editForm = this.fb.group({
    id: [],
    counterParty: [],
    currencyBuy: [],
    currencySell: [],
    rate: [],
    amount: [],
    contraAmount: [],
    valueDate: [],
    customer: [],
  });

  constructor(
    protected smartTradeService: SmartTradeService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ smartTrade }) => {
      this.updateForm(smartTrade);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const smartTrade = this.createFromForm();
    if (smartTrade.id !== undefined) {
      this.subscribeToSaveResponse(this.smartTradeService.update(smartTrade));
    } else {
      this.subscribeToSaveResponse(this.smartTradeService.create(smartTrade));
    }
  }

  trackCustomerById(_index: number, item: ICustomer): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISmartTrade>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(smartTrade: ISmartTrade): void {
    this.editForm.patchValue({
      id: smartTrade.id,
      counterParty: smartTrade.counterParty,
      currencyBuy: smartTrade.currencyBuy,
      currencySell: smartTrade.currencySell,
      rate: smartTrade.rate,
      amount: smartTrade.amount,
      contraAmount: smartTrade.contraAmount,
      valueDate: smartTrade.valueDate,
      customer: smartTrade.customer,
    });

    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing(
      this.customersSharedCollection,
      smartTrade.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing(customers, this.editForm.get('customer')!.value)
        )
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }

  protected createFromForm(): ISmartTrade {
    return {
      ...new SmartTrade(),
      id: this.editForm.get(['id'])!.value,
      counterParty: this.editForm.get(['counterParty'])!.value,
      currencyBuy: this.editForm.get(['currencyBuy'])!.value,
      currencySell: this.editForm.get(['currencySell'])!.value,
      rate: this.editForm.get(['rate'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      contraAmount: this.editForm.get(['contraAmount'])!.value,
      valueDate: this.editForm.get(['valueDate'])!.value,
      customer: this.editForm.get(['customer'])!.value,
    };
  }
}
