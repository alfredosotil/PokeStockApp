import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../market-price.test-samples';

import { MarketPriceFormService } from './market-price-form.service';

describe('MarketPrice Form Service', () => {
  let service: MarketPriceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MarketPriceFormService);
  });

  describe('Service methods', () => {
    describe('createMarketPriceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMarketPriceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            source: expect.any(Object),
            currency: expect.any(Object),
            priceLow: expect.any(Object),
            priceMid: expect.any(Object),
            priceHigh: expect.any(Object),
            lastUpdated: expect.any(Object),
            card: expect.any(Object),
          }),
        );
      });

      it('passing IMarketPrice should create a new form with FormGroup', () => {
        const formGroup = service.createMarketPriceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            source: expect.any(Object),
            currency: expect.any(Object),
            priceLow: expect.any(Object),
            priceMid: expect.any(Object),
            priceHigh: expect.any(Object),
            lastUpdated: expect.any(Object),
            card: expect.any(Object),
          }),
        );
      });
    });

    describe('getMarketPrice', () => {
      it('should return NewMarketPrice for default MarketPrice initial value', () => {
        const formGroup = service.createMarketPriceFormGroup(sampleWithNewData);

        const marketPrice = service.getMarketPrice(formGroup) as any;

        expect(marketPrice).toMatchObject(sampleWithNewData);
      });

      it('should return NewMarketPrice for empty MarketPrice initial value', () => {
        const formGroup = service.createMarketPriceFormGroup();

        const marketPrice = service.getMarketPrice(formGroup) as any;

        expect(marketPrice).toMatchObject({});
      });

      it('should return IMarketPrice', () => {
        const formGroup = service.createMarketPriceFormGroup(sampleWithRequiredData);

        const marketPrice = service.getMarketPrice(formGroup) as any;

        expect(marketPrice).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMarketPrice should not enable id FormControl', () => {
        const formGroup = service.createMarketPriceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMarketPrice should disable id FormControl', () => {
        const formGroup = service.createMarketPriceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
