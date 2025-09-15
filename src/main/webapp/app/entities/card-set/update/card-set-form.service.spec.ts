import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../card-set.test-samples';

import { CardSetFormService } from './card-set-form.service';

describe('CardSet Form Service', () => {
  let service: CardSetFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardSetFormService);
  });

  describe('Service methods', () => {
    describe('createCardSetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCardSetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            releaseDate: expect.any(Object),
          }),
        );
      });

      it('passing ICardSet should create a new form with FormGroup', () => {
        const formGroup = service.createCardSetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            releaseDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getCardSet', () => {
      it('should return NewCardSet for default CardSet initial value', () => {
        const formGroup = service.createCardSetFormGroup(sampleWithNewData);

        const cardSet = service.getCardSet(formGroup) as any;

        expect(cardSet).toMatchObject(sampleWithNewData);
      });

      it('should return NewCardSet for empty CardSet initial value', () => {
        const formGroup = service.createCardSetFormGroup();

        const cardSet = service.getCardSet(formGroup) as any;

        expect(cardSet).toMatchObject({});
      });

      it('should return ICardSet', () => {
        const formGroup = service.createCardSetFormGroup(sampleWithRequiredData);

        const cardSet = service.getCardSet(formGroup) as any;

        expect(cardSet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICardSet should not enable id FormControl', () => {
        const formGroup = service.createCardSetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCardSet should disable id FormControl', () => {
        const formGroup = service.createCardSetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
