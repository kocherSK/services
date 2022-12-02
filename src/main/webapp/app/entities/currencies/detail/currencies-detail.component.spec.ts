import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CurrenciesDetailComponent } from './currencies-detail.component';

describe('Currencies Management Detail Component', () => {
  let comp: CurrenciesDetailComponent;
  let fixture: ComponentFixture<CurrenciesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CurrenciesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ currencies: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(CurrenciesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CurrenciesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load currencies on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.currencies).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
