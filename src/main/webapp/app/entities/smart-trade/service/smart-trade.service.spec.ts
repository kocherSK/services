import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISmartTrade, SmartTrade } from '../smart-trade.model';

import { SmartTradeService } from './smart-trade.service';

describe('SmartTrade Service', () => {
  let service: SmartTradeService;
  let httpMock: HttpTestingController;
  let elemDefault: ISmartTrade;
  let expectedResult: ISmartTrade | ISmartTrade[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SmartTradeService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      counterParty: 'AAAAAAA',
      currencyBuy: 'AAAAAAA',
      currencySell: 'AAAAAAA',
      rate: 0,
      amount: 0,
      contraAmount: 0,
      valueDate: currentDate,
      transactionId: 'AAAAAAA',
      direction: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          valueDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a SmartTrade', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          valueDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          valueDate: currentDate,
        },
        returnedFromService
      );

      service.create(new SmartTrade()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SmartTrade', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          counterParty: 'BBBBBB',
          currencyBuy: 'BBBBBB',
          currencySell: 'BBBBBB',
          rate: 1,
          amount: 1,
          contraAmount: 1,
          valueDate: currentDate.format(DATE_FORMAT),
          transactionId: 'BBBBBB',
          direction: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          valueDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SmartTrade', () => {
      const patchObject = Object.assign(
        {
          counterParty: 'BBBBBB',
          currencyBuy: 'BBBBBB',
          rate: 1,
          amount: 1,
          contraAmount: 1,
          transactionId: 'BBBBBB',
          direction: 'BBBBBB',
        },
        new SmartTrade()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          valueDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SmartTrade', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          counterParty: 'BBBBBB',
          currencyBuy: 'BBBBBB',
          currencySell: 'BBBBBB',
          rate: 1,
          amount: 1,
          contraAmount: 1,
          valueDate: currentDate.format(DATE_FORMAT),
          transactionId: 'BBBBBB',
          direction: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          valueDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a SmartTrade', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSmartTradeToCollectionIfMissing', () => {
      it('should add a SmartTrade to an empty array', () => {
        const smartTrade: ISmartTrade = { id: 'ABC' };
        expectedResult = service.addSmartTradeToCollectionIfMissing([], smartTrade);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(smartTrade);
      });

      it('should not add a SmartTrade to an array that contains it', () => {
        const smartTrade: ISmartTrade = { id: 'ABC' };
        const smartTradeCollection: ISmartTrade[] = [
          {
            ...smartTrade,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addSmartTradeToCollectionIfMissing(smartTradeCollection, smartTrade);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SmartTrade to an array that doesn't contain it", () => {
        const smartTrade: ISmartTrade = { id: 'ABC' };
        const smartTradeCollection: ISmartTrade[] = [{ id: 'CBA' }];
        expectedResult = service.addSmartTradeToCollectionIfMissing(smartTradeCollection, smartTrade);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(smartTrade);
      });

      it('should add only unique SmartTrade to an array', () => {
        const smartTradeArray: ISmartTrade[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '1a773446-35c4-4a42-b9dd-d346fa07e96a' }];
        const smartTradeCollection: ISmartTrade[] = [{ id: 'ABC' }];
        expectedResult = service.addSmartTradeToCollectionIfMissing(smartTradeCollection, ...smartTradeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const smartTrade: ISmartTrade = { id: 'ABC' };
        const smartTrade2: ISmartTrade = { id: 'CBA' };
        expectedResult = service.addSmartTradeToCollectionIfMissing([], smartTrade, smartTrade2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(smartTrade);
        expect(expectedResult).toContain(smartTrade2);
      });

      it('should accept null and undefined values', () => {
        const smartTrade: ISmartTrade = { id: 'ABC' };
        expectedResult = service.addSmartTradeToCollectionIfMissing([], null, smartTrade, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(smartTrade);
      });

      it('should return initial array if no SmartTrade is added', () => {
        const smartTradeCollection: ISmartTrade[] = [{ id: 'ABC' }];
        expectedResult = service.addSmartTradeToCollectionIfMissing(smartTradeCollection, undefined, null);
        expect(expectedResult).toEqual(smartTradeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
