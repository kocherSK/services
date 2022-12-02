import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SmartTradeService } from '../service/smart-trade.service';
import { ISmartTrade, SmartTrade } from '../smart-trade.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

import { SmartTradeUpdateComponent } from './smart-trade-update.component';

describe('SmartTrade Management Update Component', () => {
  let comp: SmartTradeUpdateComponent;
  let fixture: ComponentFixture<SmartTradeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let smartTradeService: SmartTradeService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SmartTradeUpdateComponent],
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
      .overrideTemplate(SmartTradeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SmartTradeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    smartTradeService = TestBed.inject(SmartTradeService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customer query and add missing value', () => {
      const smartTrade: ISmartTrade = { id: 'CBA' };
      const customer: ICustomer = { id: '5ed90e7e-e337-4f6f-85ce-d326129641c5' };
      smartTrade.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 'e6e9f023-b6ac-4ebf-a8ec-31725707e777' }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(customerCollection, ...additionalCustomers);
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const smartTrade: ISmartTrade = { id: 'CBA' };
      const customer: ICustomer = { id: 'ac03ae22-5a0c-4e36-9393-3469f5d84b78' };
      smartTrade.customer = customer;

      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(smartTrade));
      expect(comp.customersSharedCollection).toContain(customer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SmartTrade>>();
      const smartTrade = { id: 'ABC' };
      jest.spyOn(smartTradeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: smartTrade }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(smartTradeService.update).toHaveBeenCalledWith(smartTrade);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SmartTrade>>();
      const smartTrade = new SmartTrade();
      jest.spyOn(smartTradeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: smartTrade }));
      saveSubject.complete();

      // THEN
      expect(smartTradeService.create).toHaveBeenCalledWith(smartTrade);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SmartTrade>>();
      const smartTrade = { id: 'ABC' };
      jest.spyOn(smartTradeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(smartTradeService.update).toHaveBeenCalledWith(smartTrade);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCustomerById', () => {
      it('Should return tracked Customer primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCustomerById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
