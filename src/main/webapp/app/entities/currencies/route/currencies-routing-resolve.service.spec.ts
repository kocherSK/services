import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICurrencies, Currencies } from '../currencies.model';
import { CurrenciesService } from '../service/currencies.service';

import { CurrenciesRoutingResolveService } from './currencies-routing-resolve.service';

describe('Currencies routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CurrenciesRoutingResolveService;
  let service: CurrenciesService;
  let resultCurrencies: ICurrencies | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(CurrenciesRoutingResolveService);
    service = TestBed.inject(CurrenciesService);
    resultCurrencies = undefined;
  });

  describe('resolve', () => {
    it('should return ICurrencies returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCurrencies = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCurrencies).toEqual({ id: 'ABC' });
    });

    it('should return new ICurrencies if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCurrencies = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCurrencies).toEqual(new Currencies());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Currencies })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCurrencies = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCurrencies).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
