import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICardSet } from '../card-set.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../card-set.test-samples';

import { CardSetService, RestCardSet } from './card-set.service';

const requireRestSample: RestCardSet = {
  ...sampleWithRequiredData,
  releaseDate: sampleWithRequiredData.releaseDate?.toJSON(),
};

describe('CardSet Service', () => {
  let service: CardSetService;
  let httpMock: HttpTestingController;
  let expectedResult: ICardSet | ICardSet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CardSetService);
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

    it('should create a CardSet', () => {
      const cardSet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cardSet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CardSet', () => {
      const cardSet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cardSet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CardSet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CardSet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CardSet', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCardSetToCollectionIfMissing', () => {
      it('should add a CardSet to an empty array', () => {
        const cardSet: ICardSet = sampleWithRequiredData;
        expectedResult = service.addCardSetToCollectionIfMissing([], cardSet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardSet);
      });

      it('should not add a CardSet to an array that contains it', () => {
        const cardSet: ICardSet = sampleWithRequiredData;
        const cardSetCollection: ICardSet[] = [
          {
            ...cardSet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCardSetToCollectionIfMissing(cardSetCollection, cardSet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CardSet to an array that doesn't contain it", () => {
        const cardSet: ICardSet = sampleWithRequiredData;
        const cardSetCollection: ICardSet[] = [sampleWithPartialData];
        expectedResult = service.addCardSetToCollectionIfMissing(cardSetCollection, cardSet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardSet);
      });

      it('should add only unique CardSet to an array', () => {
        const cardSetArray: ICardSet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cardSetCollection: ICardSet[] = [sampleWithRequiredData];
        expectedResult = service.addCardSetToCollectionIfMissing(cardSetCollection, ...cardSetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cardSet: ICardSet = sampleWithRequiredData;
        const cardSet2: ICardSet = sampleWithPartialData;
        expectedResult = service.addCardSetToCollectionIfMissing([], cardSet, cardSet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardSet);
        expect(expectedResult).toContain(cardSet2);
      });

      it('should accept null and undefined values', () => {
        const cardSet: ICardSet = sampleWithRequiredData;
        expectedResult = service.addCardSetToCollectionIfMissing([], null, cardSet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardSet);
      });

      it('should return initial array if no CardSet is added', () => {
        const cardSetCollection: ICardSet[] = [sampleWithRequiredData];
        expectedResult = service.addCardSetToCollectionIfMissing(cardSetCollection, undefined, null);
        expect(expectedResult).toEqual(cardSetCollection);
      });
    });

    describe('compareCardSet', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCardSet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12456 };
        const entity2 = null;

        const compareResult1 = service.compareCardSet(entity1, entity2);
        const compareResult2 = service.compareCardSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12456 };
        const entity2 = { id: 22016 };

        const compareResult1 = service.compareCardSet(entity1, entity2);
        const compareResult2 = service.compareCardSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12456 };
        const entity2 = { id: 12456 };

        const compareResult1 = service.compareCardSet(entity1, entity2);
        const compareResult2 = service.compareCardSet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
