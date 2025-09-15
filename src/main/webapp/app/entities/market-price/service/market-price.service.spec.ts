import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMarketPrice } from '../market-price.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../market-price.test-samples';

import { MarketPriceService, RestMarketPrice } from './market-price.service';

const requireRestSample: RestMarketPrice = {
  ...sampleWithRequiredData,
  lastUpdated: sampleWithRequiredData.lastUpdated?.toJSON(),
};

describe('MarketPrice Service', () => {
  let service: MarketPriceService;
  let httpMock: HttpTestingController;
  let expectedResult: IMarketPrice | IMarketPrice[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MarketPriceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a MarketPrice', () => {
      const marketPrice = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(marketPrice).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MarketPrice', () => {
      const marketPrice = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(marketPrice).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MarketPrice', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MarketPrice', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MarketPrice', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMarketPriceToCollectionIfMissing', () => {
      it('should add a MarketPrice to an empty array', () => {
        const marketPrice: IMarketPrice = sampleWithRequiredData;
        expectedResult = service.addMarketPriceToCollectionIfMissing([], marketPrice);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(marketPrice);
      });

      it('should not add a MarketPrice to an array that contains it', () => {
        const marketPrice: IMarketPrice = sampleWithRequiredData;
        const marketPriceCollection: IMarketPrice[] = [
          {
            ...marketPrice,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMarketPriceToCollectionIfMissing(marketPriceCollection, marketPrice);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MarketPrice to an array that doesn't contain it", () => {
        const marketPrice: IMarketPrice = sampleWithRequiredData;
        const marketPriceCollection: IMarketPrice[] = [sampleWithPartialData];
        expectedResult = service.addMarketPriceToCollectionIfMissing(marketPriceCollection, marketPrice);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(marketPrice);
      });

      it('should add only unique MarketPrice to an array', () => {
        const marketPriceArray: IMarketPrice[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const marketPriceCollection: IMarketPrice[] = [sampleWithRequiredData];
        expectedResult = service.addMarketPriceToCollectionIfMissing(marketPriceCollection, ...marketPriceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const marketPrice: IMarketPrice = sampleWithRequiredData;
        const marketPrice2: IMarketPrice = sampleWithPartialData;
        expectedResult = service.addMarketPriceToCollectionIfMissing([], marketPrice, marketPrice2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(marketPrice);
        expect(expectedResult).toContain(marketPrice2);
      });

      it('should accept null and undefined values', () => {
        const marketPrice: IMarketPrice = sampleWithRequiredData;
        expectedResult = service.addMarketPriceToCollectionIfMissing([], null, marketPrice, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(marketPrice);
      });

      it('should return initial array if no MarketPrice is added', () => {
        const marketPriceCollection: IMarketPrice[] = [sampleWithRequiredData];
        expectedResult = service.addMarketPriceToCollectionIfMissing(marketPriceCollection, undefined, null);
        expect(expectedResult).toEqual(marketPriceCollection);
      });
    });

    describe('compareMarketPrice', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMarketPrice(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20939 };
        const entity2 = null;

        const compareResult1 = service.compareMarketPrice(entity1, entity2);
        const compareResult2 = service.compareMarketPrice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20939 };
        const entity2 = { id: 26418 };

        const compareResult1 = service.compareMarketPrice(entity1, entity2);
        const compareResult2 = service.compareMarketPrice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20939 };
        const entity2 = { id: 20939 };

        const compareResult1 = service.compareMarketPrice(entity1, entity2);
        const compareResult2 = service.compareMarketPrice(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
