import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMarketPrice, NewMarketPrice } from '../market-price.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMarketPrice for edit and NewMarketPriceFormGroupInput for create.
 */
type MarketPriceFormGroupInput = IMarketPrice | PartialWithRequiredKeyOf<NewMarketPrice>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMarketPrice | NewMarketPrice> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

type MarketPriceFormRawValue = FormValueOf<IMarketPrice>;

type NewMarketPriceFormRawValue = FormValueOf<NewMarketPrice>;

type MarketPriceFormDefaults = Pick<NewMarketPrice, 'id' | 'lastUpdated'>;

type MarketPriceFormGroupContent = {
  id: FormControl<MarketPriceFormRawValue['id'] | NewMarketPrice['id']>;
  source: FormControl<MarketPriceFormRawValue['source']>;
  currency: FormControl<MarketPriceFormRawValue['currency']>;
  priceLow: FormControl<MarketPriceFormRawValue['priceLow']>;
  priceMid: FormControl<MarketPriceFormRawValue['priceMid']>;
  priceHigh: FormControl<MarketPriceFormRawValue['priceHigh']>;
  lastUpdated: FormControl<MarketPriceFormRawValue['lastUpdated']>;
  card: FormControl<MarketPriceFormRawValue['card']>;
};

export type MarketPriceFormGroup = FormGroup<MarketPriceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MarketPriceFormService {
  createMarketPriceFormGroup(marketPrice: MarketPriceFormGroupInput = { id: null }): MarketPriceFormGroup {
    const marketPriceRawValue = this.convertMarketPriceToMarketPriceRawValue({
      ...this.getFormDefaults(),
      ...marketPrice,
    });
    return new FormGroup<MarketPriceFormGroupContent>({
      id: new FormControl(
        { value: marketPriceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      source: new FormControl(marketPriceRawValue.source, {
        validators: [Validators.required],
      }),
      currency: new FormControl(marketPriceRawValue.currency, {
        validators: [Validators.required],
      }),
      priceLow: new FormControl(marketPriceRawValue.priceLow),
      priceMid: new FormControl(marketPriceRawValue.priceMid),
      priceHigh: new FormControl(marketPriceRawValue.priceHigh),
      lastUpdated: new FormControl(marketPriceRawValue.lastUpdated, {
        validators: [Validators.required],
      }),
      card: new FormControl(marketPriceRawValue.card),
    });
  }

  getMarketPrice(form: MarketPriceFormGroup): IMarketPrice | NewMarketPrice {
    return this.convertMarketPriceRawValueToMarketPrice(form.getRawValue() as MarketPriceFormRawValue | NewMarketPriceFormRawValue);
  }

  resetForm(form: MarketPriceFormGroup, marketPrice: MarketPriceFormGroupInput): void {
    const marketPriceRawValue = this.convertMarketPriceToMarketPriceRawValue({ ...this.getFormDefaults(), ...marketPrice });
    form.reset(
      {
        ...marketPriceRawValue,
        id: { value: marketPriceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MarketPriceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdated: currentTime,
    };
  }

  private convertMarketPriceRawValueToMarketPrice(
    rawMarketPrice: MarketPriceFormRawValue | NewMarketPriceFormRawValue,
  ): IMarketPrice | NewMarketPrice {
    return {
      ...rawMarketPrice,
      lastUpdated: dayjs(rawMarketPrice.lastUpdated, DATE_TIME_FORMAT),
    };
  }

  private convertMarketPriceToMarketPriceRawValue(
    marketPrice: IMarketPrice | (Partial<NewMarketPrice> & MarketPriceFormDefaults),
  ): MarketPriceFormRawValue | PartialWithRequiredKeyOf<NewMarketPriceFormRawValue> {
    return {
      ...marketPrice,
      lastUpdated: marketPrice.lastUpdated ? marketPrice.lastUpdated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
