import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../trade-item.test-samples';

import { TradeItemFormService } from './trade-item-form.service';

describe('TradeItem Form Service', () => {
  let service: TradeItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TradeItemFormService);
  });

  describe('Service methods', () => {
    describe('createTradeItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTradeItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            side: expect.any(Object),
            trade: expect.any(Object),
            card: expect.any(Object),
          }),
        );
      });

      it('passing ITradeItem should create a new form with FormGroup', () => {
        const formGroup = service.createTradeItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            side: expect.any(Object),
            trade: expect.any(Object),
            card: expect.any(Object),
          }),
        );
      });
    });

    describe('getTradeItem', () => {
      it('should return NewTradeItem for default TradeItem initial value', () => {
        const formGroup = service.createTradeItemFormGroup(sampleWithNewData);

        const tradeItem = service.getTradeItem(formGroup) as any;

        expect(tradeItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewTradeItem for empty TradeItem initial value', () => {
        const formGroup = service.createTradeItemFormGroup();

        const tradeItem = service.getTradeItem(formGroup) as any;

        expect(tradeItem).toMatchObject({});
      });

      it('should return ITradeItem', () => {
        const formGroup = service.createTradeItemFormGroup(sampleWithRequiredData);

        const tradeItem = service.getTradeItem(formGroup) as any;

        expect(tradeItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITradeItem should not enable id FormControl', () => {
        const formGroup = service.createTradeItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTradeItem should disable id FormControl', () => {
        const formGroup = service.createTradeItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
