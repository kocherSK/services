import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SmartTradeService } from '../service/smart-trade.service';

import { SmartTradeComponent } from './smart-trade.component';

describe('SmartTrade Management Component', () => {
  let comp: SmartTradeComponent;
  let fixture: ComponentFixture<SmartTradeComponent>;
  let service: SmartTradeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [SmartTradeComponent],
    })
      .overrideTemplate(SmartTradeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SmartTradeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SmartTradeService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.smartTrades?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
