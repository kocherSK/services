import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartTradeDetailComponent } from './smart-trade-detail.component';

describe('SmartTrade Management Detail Component', () => {
  let comp: SmartTradeDetailComponent;
  let fixture: ComponentFixture<SmartTradeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SmartTradeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ smartTrade: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(SmartTradeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SmartTradeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load smartTrade on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.smartTrade).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
