import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrade, NewTrade } from '../trade.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrade for edit and NewTradeFormGroupInput for create.
 */
type TradeFormGroupInput = ITrade | PartialWithRequiredKeyOf<NewTrade>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrade | NewTrade> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type TradeFormRawValue = FormValueOf<ITrade>;

type NewTradeFormRawValue = FormValueOf<NewTrade>;

type TradeFormDefaults = Pick<NewTrade, 'id' | 'createdAt' | 'updatedAt'>;

type TradeFormGroupContent = {
  id: FormControl<TradeFormRawValue['id'] | NewTrade['id']>;
  status: FormControl<TradeFormRawValue['status']>;
  message: FormControl<TradeFormRawValue['message']>;
  createdAt: FormControl<TradeFormRawValue['createdAt']>;
  updatedAt: FormControl<TradeFormRawValue['updatedAt']>;
  proposer: FormControl<TradeFormRawValue['proposer']>;
};

export type TradeFormGroup = FormGroup<TradeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TradeFormService {
  createTradeFormGroup(trade: TradeFormGroupInput = { id: null }): TradeFormGroup {
    const tradeRawValue = this.convertTradeToTradeRawValue({
      ...this.getFormDefaults(),
      ...trade,
    });
    return new FormGroup<TradeFormGroupContent>({
      id: new FormControl(
        { value: tradeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      status: new FormControl(tradeRawValue.status, {
        validators: [Validators.required],
      }),
      message: new FormControl(tradeRawValue.message),
      createdAt: new FormControl(tradeRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(tradeRawValue.updatedAt),
      proposer: new FormControl(tradeRawValue.proposer),
    });
  }

  getTrade(form: TradeFormGroup): ITrade | NewTrade {
    return this.convertTradeRawValueToTrade(form.getRawValue() as TradeFormRawValue | NewTradeFormRawValue);
  }

  resetForm(form: TradeFormGroup, trade: TradeFormGroupInput): void {
    const tradeRawValue = this.convertTradeToTradeRawValue({ ...this.getFormDefaults(), ...trade });
    form.reset(
      {
        ...tradeRawValue,
        id: { value: tradeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TradeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertTradeRawValueToTrade(rawTrade: TradeFormRawValue | NewTradeFormRawValue): ITrade | NewTrade {
    return {
      ...rawTrade,
      createdAt: dayjs(rawTrade.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawTrade.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertTradeToTradeRawValue(
    trade: ITrade | (Partial<NewTrade> & TradeFormDefaults),
  ): TradeFormRawValue | PartialWithRequiredKeyOf<NewTradeFormRawValue> {
    return {
      ...trade,
      createdAt: trade.createdAt ? trade.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: trade.updatedAt ? trade.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
