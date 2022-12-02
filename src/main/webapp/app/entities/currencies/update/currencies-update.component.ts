import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICurrencies, Currencies } from '../currencies.model';
import { CurrenciesService } from '../service/currencies.service';

@Component({
  selector: 'jhi-currencies-update',
  templateUrl: './currencies-update.component.html',
})
export class CurrenciesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    currencyName: [],
    currencyCode: [],
  });

  constructor(protected currenciesService: CurrenciesService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ currencies }) => {
      this.updateForm(currencies);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const currencies = this.createFromForm();
    if (currencies.id !== undefined) {
      this.subscribeToSaveResponse(this.currenciesService.update(currencies));
    } else {
      this.subscribeToSaveResponse(this.currenciesService.create(currencies));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICurrencies>>): void {
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

  protected updateForm(currencies: ICurrencies): void {
    this.editForm.patchValue({
      id: currencies.id,
      currencyName: currencies.currencyName,
      currencyCode: currencies.currencyCode,
    });
  }

  protected createFromForm(): ICurrencies {
    return {
      ...new Currencies(),
      id: this.editForm.get(['id'])!.value,
      currencyName: this.editForm.get(['currencyName'])!.value,
      currencyCode: this.editForm.get(['currencyCode'])!.value,
    };
  }
}
