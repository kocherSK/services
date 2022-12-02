import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICurrencies, Currencies } from '../currencies.model';

import { CurrenciesService } from './currencies.service';

describe('Currencies Service', () => {
  let service: CurrenciesService;
  let httpMock: HttpTestingController;
  let elemDefault: ICurrencies;
  let expectedResult: ICurrencies | ICurrencies[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CurrenciesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      currencyName: 'AAAAAAA',
      currencyCode: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Currencies', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Currencies()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Currencies', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          currencyName: 'BBBBBB',
          currencyCode: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Currencies', () => {
      const patchObject = Object.assign({}, new Currencies());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Currencies', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          currencyName: 'BBBBBB',
          currencyCode: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Currencies', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCurrenciesToCollectionIfMissing', () => {
      it('should add a Currencies to an empty array', () => {
        const currencies: ICurrencies = { id: 'ABC' };
        expectedResult = service.addCurrenciesToCollectionIfMissing([], currencies);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(currencies);
      });

      it('should not add a Currencies to an array that contains it', () => {
        const currencies: ICurrencies = { id: 'ABC' };
        const currenciesCollection: ICurrencies[] = [
          {
            ...currencies,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCurrenciesToCollectionIfMissing(currenciesCollection, currencies);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Currencies to an array that doesn't contain it", () => {
        const currencies: ICurrencies = { id: 'ABC' };
        const currenciesCollection: ICurrencies[] = [{ id: 'CBA' }];
        expectedResult = service.addCurrenciesToCollectionIfMissing(currenciesCollection, currencies);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(currencies);
      });

      it('should add only unique Currencies to an array', () => {
        const currenciesArray: ICurrencies[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '8ab1e34b-59e1-42d9-b9bd-a106fb9f7c1e' }];
        const currenciesCollection: ICurrencies[] = [{ id: 'ABC' }];
        expectedResult = service.addCurrenciesToCollectionIfMissing(currenciesCollection, ...currenciesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const currencies: ICurrencies = { id: 'ABC' };
        const currencies2: ICurrencies = { id: 'CBA' };
        expectedResult = service.addCurrenciesToCollectionIfMissing([], currencies, currencies2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(currencies);
        expect(expectedResult).toContain(currencies2);
      });

      it('should accept null and undefined values', () => {
        const currencies: ICurrencies = { id: 'ABC' };
        expectedResult = service.addCurrenciesToCollectionIfMissing([], null, currencies, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(currencies);
      });

      it('should return initial array if no Currencies is added', () => {
        const currenciesCollection: ICurrencies[] = [{ id: 'ABC' }];
        expectedResult = service.addCurrenciesToCollectionIfMissing(currenciesCollection, undefined, null);
        expect(expectedResult).toEqual(currenciesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
