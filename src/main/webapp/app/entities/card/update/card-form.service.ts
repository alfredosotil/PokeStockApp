import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICard, NewCard } from '../card.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICard for edit and NewCardFormGroupInput for create.
 */
type CardFormGroupInput = ICard | PartialWithRequiredKeyOf<NewCard>;

type CardFormDefaults = Pick<NewCard, 'id'>;

type CardFormGroupContent = {
  id: FormControl<ICard['id'] | NewCard['id']>;
  tcgId: FormControl<ICard['tcgId']>;
  setCode: FormControl<ICard['setCode']>;
  number: FormControl<ICard['number']>;
  name: FormControl<ICard['name']>;
  rarity: FormControl<ICard['rarity']>;
  superType: FormControl<ICard['superType']>;
  types: FormControl<ICard['types']>;
  imageUrl: FormControl<ICard['imageUrl']>;
  legalities: FormControl<ICard['legalities']>;
  set: FormControl<ICard['set']>;
};

export type CardFormGroup = FormGroup<CardFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CardFormService {
  createCardFormGroup(card: CardFormGroupInput = { id: null }): CardFormGroup {
    const cardRawValue = {
      ...this.getFormDefaults(),
      ...card,
    };
    return new FormGroup<CardFormGroupContent>({
      id: new FormControl(
        { value: cardRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tcgId: new FormControl(cardRawValue.tcgId, {
        validators: [Validators.required],
      }),
      setCode: new FormControl(cardRawValue.setCode, {
        validators: [Validators.required],
      }),
      number: new FormControl(cardRawValue.number, {
        validators: [Validators.required],
      }),
      name: new FormControl(cardRawValue.name, {
        validators: [Validators.required],
      }),
      rarity: new FormControl(cardRawValue.rarity),
      superType: new FormControl(cardRawValue.superType),
      types: new FormControl(cardRawValue.types),
      imageUrl: new FormControl(cardRawValue.imageUrl),
      legalities: new FormControl(cardRawValue.legalities),
      set: new FormControl(cardRawValue.set),
    });
  }

  getCard(form: CardFormGroup): ICard | NewCard {
    return form.getRawValue() as ICard | NewCard;
  }

  resetForm(form: CardFormGroup, card: CardFormGroupInput): void {
    const cardRawValue = { ...this.getFormDefaults(), ...card };
    form.reset(
      {
        ...cardRawValue,
        id: { value: cardRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CardFormDefaults {
    return {
      id: null,
    };
  }
}
