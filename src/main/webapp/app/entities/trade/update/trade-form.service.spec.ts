import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../trade.test-samples';

import { TradeFormService } from './trade-form.service';

describe('Trade Form Service', () => {
  let service: TradeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TradeFormService);
  });

  describe('Service methods', () => {
    describe('createTradeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTradeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            message: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            proposer: expect.any(Object),
          }),
        );
      });

      it('passing ITrade should create a new form with FormGroup', () => {
        const formGroup = service.createTradeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            message: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            proposer: expect.any(Object),
          }),
        );
      });
    });

    describe('getTrade', () => {
      it('should return NewTrade for default Trade initial value', () => {
        const formGroup = service.createTradeFormGroup(sampleWithNewData);

        const trade = service.getTrade(formGroup) as any;

        expect(trade).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrade for empty Trade initial value', () => {
        const formGroup = service.createTradeFormGroup();

        const trade = service.getTrade(formGroup) as any;

        expect(trade).toMatchObject({});
      });

      it('should return ITrade', () => {
        const formGroup = service.createTradeFormGroup(sampleWithRequiredData);

        const trade = service.getTrade(formGroup) as any;

        expect(trade).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrade should not enable id FormControl', () => {
        const formGroup = service.createTradeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrade should disable id FormControl', () => {
        const formGroup = service.createTradeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
