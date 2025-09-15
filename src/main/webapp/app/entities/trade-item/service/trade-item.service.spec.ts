import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITradeItem } from '../trade-item.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../trade-item.test-samples';

import { TradeItemService } from './trade-item.service';

const requireRestSample: ITradeItem = {
  ...sampleWithRequiredData,
};

describe('TradeItem Service', () => {
  let service: TradeItemService;
  let httpMock: HttpTestingController;
  let expectedResult: ITradeItem | ITradeItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TradeItemService);
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

    it('should create a TradeItem', () => {
      const tradeItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tradeItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TradeItem', () => {
      const tradeItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tradeItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TradeItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TradeItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TradeItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTradeItemToCollectionIfMissing', () => {
      it('should add a TradeItem to an empty array', () => {
        const tradeItem: ITradeItem = sampleWithRequiredData;
        expectedResult = service.addTradeItemToCollectionIfMissing([], tradeItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tradeItem);
      });

      it('should not add a TradeItem to an array that contains it', () => {
        const tradeItem: ITradeItem = sampleWithRequiredData;
        const tradeItemCollection: ITradeItem[] = [
          {
            ...tradeItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTradeItemToCollectionIfMissing(tradeItemCollection, tradeItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TradeItem to an array that doesn't contain it", () => {
        const tradeItem: ITradeItem = sampleWithRequiredData;
        const tradeItemCollection: ITradeItem[] = [sampleWithPartialData];
        expectedResult = service.addTradeItemToCollectionIfMissing(tradeItemCollection, tradeItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tradeItem);
      });

      it('should add only unique TradeItem to an array', () => {
        const tradeItemArray: ITradeItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tradeItemCollection: ITradeItem[] = [sampleWithRequiredData];
        expectedResult = service.addTradeItemToCollectionIfMissing(tradeItemCollection, ...tradeItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tradeItem: ITradeItem = sampleWithRequiredData;
        const tradeItem2: ITradeItem = sampleWithPartialData;
        expectedResult = service.addTradeItemToCollectionIfMissing([], tradeItem, tradeItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tradeItem);
        expect(expectedResult).toContain(tradeItem2);
      });

      it('should accept null and undefined values', () => {
        const tradeItem: ITradeItem = sampleWithRequiredData;
        expectedResult = service.addTradeItemToCollectionIfMissing([], null, tradeItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tradeItem);
      });

      it('should return initial array if no TradeItem is added', () => {
        const tradeItemCollection: ITradeItem[] = [sampleWithRequiredData];
        expectedResult = service.addTradeItemToCollectionIfMissing(tradeItemCollection, undefined, null);
        expect(expectedResult).toEqual(tradeItemCollection);
      });
    });

    describe('compareTradeItem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTradeItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25905 };
        const entity2 = null;

        const compareResult1 = service.compareTradeItem(entity1, entity2);
        const compareResult2 = service.compareTradeItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25905 };
        const entity2 = { id: 5123 };

        const compareResult1 = service.compareTradeItem(entity1, entity2);
        const compareResult2 = service.compareTradeItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25905 };
        const entity2 = { id: 25905 };

        const compareResult1 = service.compareTradeItem(entity1, entity2);
        const compareResult2 = service.compareTradeItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
