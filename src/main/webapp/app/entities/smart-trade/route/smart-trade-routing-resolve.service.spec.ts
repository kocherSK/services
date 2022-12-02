import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISmartTrade, SmartTrade } from '../smart-trade.model';
import { SmartTradeService } from '../service/smart-trade.service';

import { SmartTradeRoutingResolveService } from './smart-trade-routing-resolve.service';

describe('SmartTrade routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SmartTradeRoutingResolveService;
  let service: SmartTradeService;
  let resultSmartTrade: ISmartTrade | undefined;

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
    routingResolveService = TestBed.inject(SmartTradeRoutingResolveService);
    service = TestBed.inject(SmartTradeService);
    resultSmartTrade = undefined;
  });

  describe('resolve', () => {
    it('should return ISmartTrade returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSmartTrade = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultSmartTrade).toEqual({ id: 'ABC' });
    });

    it('should return new ISmartTrade if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSmartTrade = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSmartTrade).toEqual(new SmartTrade());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as SmartTrade })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSmartTrade = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultSmartTrade).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
