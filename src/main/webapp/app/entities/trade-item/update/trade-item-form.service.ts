import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITradeItem, NewTradeItem } from '../trade-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITradeItem for edit and NewTradeItemFormGroupInput for create.
 */
type TradeItemFormGroupInput = ITradeItem | PartialWithRequiredKeyOf<NewTradeItem>;

type TradeItemFormDefaults = Pick<NewTradeItem, 'id'>;

type TradeItemFormGroupContent = {
  id: FormControl<ITradeItem['id'] | NewTradeItem['id']>;
  quantity: FormControl<ITradeItem['quantity']>;
  side: FormControl<ITradeItem['side']>;
  trade: FormControl<ITradeItem['trade']>;
  card: FormControl<ITradeItem['card']>;
};

export type TradeItemFormGroup = FormGroup<TradeItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TradeItemFormService {
  createTradeItemFormGroup(tradeItem: TradeItemFormGroupInput = { id: null }): TradeItemFormGroup {
    const tradeItemRawValue = {
      ...this.getFormDefaults(),
      ...tradeItem,
    };
    return new FormGroup<TradeItemFormGroupContent>({
      id: new FormControl(
        { value: tradeItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(tradeItemRawValue.quantity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      side: new FormControl(tradeItemRawValue.side, {
        validators: [Validators.required],
      }),
      trade: new FormControl(tradeItemRawValue.trade),
      card: new FormControl(tradeItemRawValue.card),
    });
  }

  getTradeItem(form: TradeItemFormGroup): ITradeItem | NewTradeItem {
    return form.getRawValue() as ITradeItem | NewTradeItem;
  }

  resetForm(form: TradeItemFormGroup, tradeItem: TradeItemFormGroupInput): void {
    const tradeItemRawValue = { ...this.getFormDefaults(), ...tradeItem };
    form.reset(
      {
        ...tradeItemRawValue,
        id: { value: tradeItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TradeItemFormDefaults {
    return {
      id: null,
    };
  }
}
