import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CurrenciesService } from '../service/currencies.service';
import { ICurrencies, Currencies } from '../currencies.model';

import { CurrenciesUpdateComponent } from './currencies-update.component';

describe('Currencies Management Update Component', () => {
  let comp: CurrenciesUpdateComponent;
  let fixture: ComponentFixture<CurrenciesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let currenciesService: CurrenciesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CurrenciesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CurrenciesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CurrenciesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    currenciesService = TestBed.inject(CurrenciesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const currencies: ICurrencies = { id: 'CBA' };

      activatedRoute.data = of({ currencies });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(currencies));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Currencies>>();
      const currencies = { id: 'ABC' };
      jest.spyOn(currenciesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currencies });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: currencies }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(currenciesService.update).toHaveBeenCalledWith(currencies);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Currencies>>();
      const currencies = new Currencies();
      jest.spyOn(currenciesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currencies });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: currencies }));
      saveSubject.complete();

      // THEN
      expect(currenciesService.create).toHaveBeenCalledWith(currencies);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Currencies>>();
      const currencies = { id: 'ABC' };
      jest.spyOn(currenciesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currencies });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(currenciesService.update).toHaveBeenCalledWith(currencies);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
