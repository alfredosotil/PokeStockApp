import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICardSet, NewCardSet } from '../card-set.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICardSet for edit and NewCardSetFormGroupInput for create.
 */
type CardSetFormGroupInput = ICardSet | PartialWithRequiredKeyOf<NewCardSet>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICardSet | NewCardSet> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

type CardSetFormRawValue = FormValueOf<ICardSet>;

type NewCardSetFormRawValue = FormValueOf<NewCardSet>;

type CardSetFormDefaults = Pick<NewCardSet, 'id' | 'releaseDate'>;

type CardSetFormGroupContent = {
  id: FormControl<CardSetFormRawValue['id'] | NewCardSet['id']>;
  code: FormControl<CardSetFormRawValue['code']>;
  name: FormControl<CardSetFormRawValue['name']>;
  releaseDate: FormControl<CardSetFormRawValue['releaseDate']>;
};

export type CardSetFormGroup = FormGroup<CardSetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CardSetFormService {
  createCardSetFormGroup(cardSet: CardSetFormGroupInput = { id: null }): CardSetFormGroup {
    const cardSetRawValue = this.convertCardSetToCardSetRawValue({
      ...this.getFormDefaults(),
      ...cardSet,
    });
    return new FormGroup<CardSetFormGroupContent>({
      id: new FormControl(
        { value: cardSetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(cardSetRawValue.code, {
        validators: [Validators.required],
      }),
      name: new FormControl(cardSetRawValue.name, {
        validators: [Validators.required],
      }),
      releaseDate: new FormControl(cardSetRawValue.releaseDate),
    });
  }

  getCardSet(form: CardSetFormGroup): ICardSet | NewCardSet {
    return this.convertCardSetRawValueToCardSet(form.getRawValue() as CardSetFormRawValue | NewCardSetFormRawValue);
  }

  resetForm(form: CardSetFormGroup, cardSet: CardSetFormGroupInput): void {
    const cardSetRawValue = this.convertCardSetToCardSetRawValue({ ...this.getFormDefaults(), ...cardSet });
    form.reset(
      {
        ...cardSetRawValue,
        id: { value: cardSetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CardSetFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      releaseDate: currentTime,
    };
  }

  private convertCardSetRawValueToCardSet(rawCardSet: CardSetFormRawValue | NewCardSetFormRawValue): ICardSet | NewCardSet {
    return {
      ...rawCardSet,
      releaseDate: dayjs(rawCardSet.releaseDate, DATE_TIME_FORMAT),
    };
  }

  private convertCardSetToCardSetRawValue(
    cardSet: ICardSet | (Partial<NewCardSet> & CardSetFormDefaults),
  ): CardSetFormRawValue | PartialWithRequiredKeyOf<NewCardSetFormRawValue> {
    return {
      ...cardSet,
      releaseDate: cardSet.releaseDate ? cardSet.releaseDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
