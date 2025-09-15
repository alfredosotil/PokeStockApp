import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPokeUser } from '../poke-user.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../poke-user.test-samples';

import { PokeUserService } from './poke-user.service';

const requireRestSample: IPokeUser = {
  ...sampleWithRequiredData,
};

describe('PokeUser Service', () => {
  let service: PokeUserService;
  let httpMock: HttpTestingController;
  let expectedResult: IPokeUser | IPokeUser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PokeUserService);
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

    it('should create a PokeUser', () => {
      const pokeUser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pokeUser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PokeUser', () => {
      const pokeUser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pokeUser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PokeUser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PokeUser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PokeUser', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPokeUserToCollectionIfMissing', () => {
      it('should add a PokeUser to an empty array', () => {
        const pokeUser: IPokeUser = sampleWithRequiredData;
        expectedResult = service.addPokeUserToCollectionIfMissing([], pokeUser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pokeUser);
      });

      it('should not add a PokeUser to an array that contains it', () => {
        const pokeUser: IPokeUser = sampleWithRequiredData;
        const pokeUserCollection: IPokeUser[] = [
          {
            ...pokeUser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPokeUserToCollectionIfMissing(pokeUserCollection, pokeUser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PokeUser to an array that doesn't contain it", () => {
        const pokeUser: IPokeUser = sampleWithRequiredData;
        const pokeUserCollection: IPokeUser[] = [sampleWithPartialData];
        expectedResult = service.addPokeUserToCollectionIfMissing(pokeUserCollection, pokeUser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pokeUser);
      });

      it('should add only unique PokeUser to an array', () => {
        const pokeUserArray: IPokeUser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pokeUserCollection: IPokeUser[] = [sampleWithRequiredData];
        expectedResult = service.addPokeUserToCollectionIfMissing(pokeUserCollection, ...pokeUserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pokeUser: IPokeUser = sampleWithRequiredData;
        const pokeUser2: IPokeUser = sampleWithPartialData;
        expectedResult = service.addPokeUserToCollectionIfMissing([], pokeUser, pokeUser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pokeUser);
        expect(expectedResult).toContain(pokeUser2);
      });

      it('should accept null and undefined values', () => {
        const pokeUser: IPokeUser = sampleWithRequiredData;
        expectedResult = service.addPokeUserToCollectionIfMissing([], null, pokeUser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pokeUser);
      });

      it('should return initial array if no PokeUser is added', () => {
        const pokeUserCollection: IPokeUser[] = [sampleWithRequiredData];
        expectedResult = service.addPokeUserToCollectionIfMissing(pokeUserCollection, undefined, null);
        expect(expectedResult).toEqual(pokeUserCollection);
      });
    });

    describe('comparePokeUser', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePokeUser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15664 };
        const entity2 = null;

        const compareResult1 = service.comparePokeUser(entity1, entity2);
        const compareResult2 = service.comparePokeUser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15664 };
        const entity2 = { id: 27644 };

        const compareResult1 = service.comparePokeUser(entity1, entity2);
        const compareResult2 = service.comparePokeUser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15664 };
        const entity2 = { id: 15664 };

        const compareResult1 = service.comparePokeUser(entity1, entity2);
        const compareResult2 = service.comparePokeUser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
