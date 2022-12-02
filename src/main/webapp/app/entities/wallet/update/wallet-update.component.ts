import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IWallet, Wallet } from '../wallet.model';
import { WalletService } from '../service/wallet.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

@Component({
  selector: 'jhi-wallet-update',
  templateUrl: './wallet-update.component.html',
})
export class WalletUpdateComponent implements OnInit {
  isSaving = false;

  customersCollection: ICustomer[] = [];

  editForm = this.fb.group({
    id: [],
    currencyCode: [],
    amount: [],
    customer: [],
  });

  constructor(
    protected walletService: WalletService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wallet }) => {
      this.updateForm(wallet);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wallet = this.createFromForm();
    if (wallet.id !== undefined) {
      this.subscribeToSaveResponse(this.walletService.update(wallet));
    } else {
      this.subscribeToSaveResponse(this.walletService.create(wallet));
    }
  }

  trackCustomerById(_index: number, item: ICustomer): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWallet>>): void {
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

  protected updateForm(wallet: IWallet): void {
    this.editForm.patchValue({
      id: wallet.id,
      currencyCode: wallet.currencyCode,
      amount: wallet.amount,
      customer: wallet.customer,
    });

    this.customersCollection = this.customerService.addCustomerToCollectionIfMissing(this.customersCollection, wallet.customer);
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query({ filter: 'wallet-is-null' })
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing(customers, this.editForm.get('customer')!.value)
        )
      )
      .subscribe((customers: ICustomer[]) => (this.customersCollection = customers));
  }

  protected createFromForm(): IWallet {
    return {
      ...new Wallet(),
      id: this.editForm.get(['id'])!.value,
      currencyCode: this.editForm.get(['currencyCode'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      customer: this.editForm.get(['customer'])!.value,
    };
  }
}
