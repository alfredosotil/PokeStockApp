import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../poke-user.test-samples';

import { PokeUserFormService } from './poke-user-form.service';

describe('PokeUser Form Service', () => {
  let service: PokeUserFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PokeUserFormService);
  });

  describe('Service methods', () => {
    describe('createPokeUserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPokeUserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            displayName: expect.any(Object),
            country: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IPokeUser should create a new form with FormGroup', () => {
        const formGroup = service.createPokeUserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            displayName: expect.any(Object),
            country: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getPokeUser', () => {
      it('should return NewPokeUser for default PokeUser initial value', () => {
        const formGroup = service.createPokeUserFormGroup(sampleWithNewData);

        const pokeUser = service.getPokeUser(formGroup) as any;

        expect(pokeUser).toMatchObject(sampleWithNewData);
      });

      it('should return NewPokeUser for empty PokeUser initial value', () => {
        const formGroup = service.createPokeUserFormGroup();

        const pokeUser = service.getPokeUser(formGroup) as any;

        expect(pokeUser).toMatchObject({});
      });

      it('should return IPokeUser', () => {
        const formGroup = service.createPokeUserFormGroup(sampleWithRequiredData);

        const pokeUser = service.getPokeUser(formGroup) as any;

        expect(pokeUser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPokeUser should not enable id FormControl', () => {
        const formGroup = service.createPokeUserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPokeUser should disable id FormControl', () => {
        const formGroup = service.createPokeUserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
