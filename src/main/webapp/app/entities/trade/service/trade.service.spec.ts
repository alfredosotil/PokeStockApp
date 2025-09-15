import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITrade } from '../trade.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../trade.test-samples';

import { RestTrade, TradeService } from './trade.service';

const requireRestSample: RestTrade = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('Trade Service', () => {
  let service: TradeService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrade | ITrade[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TradeService);
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

    it('should create a Trade', () => {
      const trade = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trade).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Trade', () => {
      const trade = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trade).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Trade', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Trade', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Trade', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTradeToCollectionIfMissing', () => {
      it('should add a Trade to an empty array', () => {
        const trade: ITrade = sampleWithRequiredData;
        expectedResult = service.addTradeToCollectionIfMissing([], trade);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trade);
      });

      it('should not add a Trade to an array that contains it', () => {
        const trade: ITrade = sampleWithRequiredData;
        const tradeCollection: ITrade[] = [
          {
            ...trade,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTradeToCollectionIfMissing(tradeCollection, trade);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Trade to an array that doesn't contain it", () => {
        const trade: ITrade = sampleWithRequiredData;
        const tradeCollection: ITrade[] = [sampleWithPartialData];
        expectedResult = service.addTradeToCollectionIfMissing(tradeCollection, trade);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trade);
      });

      it('should add only unique Trade to an array', () => {
        const tradeArray: ITrade[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tradeCollection: ITrade[] = [sampleWithRequiredData];
        expectedResult = service.addTradeToCollectionIfMissing(tradeCollection, ...tradeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trade: ITrade = sampleWithRequiredData;
        const trade2: ITrade = sampleWithPartialData;
        expectedResult = service.addTradeToCollectionIfMissing([], trade, trade2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trade);
        expect(expectedResult).toContain(trade2);
      });

      it('should accept null and undefined values', () => {
        const trade: ITrade = sampleWithRequiredData;
        expectedResult = service.addTradeToCollectionIfMissing([], null, trade, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trade);
      });

      it('should return initial array if no Trade is added', () => {
        const tradeCollection: ITrade[] = [sampleWithRequiredData];
        expectedResult = service.addTradeToCollectionIfMissing(tradeCollection, undefined, null);
        expect(expectedResult).toEqual(tradeCollection);
      });
    });

    describe('compareTrade', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrade(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21063 };
        const entity2 = null;

        const compareResult1 = service.compareTrade(entity1, entity2);
        const compareResult2 = service.compareTrade(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21063 };
        const entity2 = { id: 30501 };

        const compareResult1 = service.compareTrade(entity1, entity2);
        const compareResult2 = service.compareTrade(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21063 };
        const entity2 = { id: 21063 };

        const compareResult1 = service.compareTrade(entity1, entity2);
        const compareResult2 = service.compareTrade(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
