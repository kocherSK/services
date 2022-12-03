import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SmartTradeService } from '../service/smart-trade.service';
import { ISmartTrade, SmartTrade } from '../smart-trade.model';

import { SmartTradeUpdateComponent } from './smart-trade-update.component';

describe('SmartTrade Management Update Component', () => {
  let comp: SmartTradeUpdateComponent;
  let fixture: ComponentFixture<SmartTradeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let smartTradeService: SmartTradeService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const smartTrade: ISmartTrade = { id: 'CBA' };

      activatedRoute.data = of({ smartTrade });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(smartTrade));
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
});
