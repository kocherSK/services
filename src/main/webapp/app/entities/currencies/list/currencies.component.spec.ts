import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CurrenciesService } from '../service/currencies.service';

import { CurrenciesComponent } from './currencies.component';

describe('Currencies Management Component', () => {
  let comp: CurrenciesComponent;
  let fixture: ComponentFixture<CurrenciesComponent>;
  let service: CurrenciesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CurrenciesComponent],
    })
      .overrideTemplate(CurrenciesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CurrenciesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CurrenciesService);

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
    expect(comp.currencies?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
