import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IInventoryItem, NewInventoryItem } from '../inventory-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInventoryItem for edit and NewInventoryItemFormGroupInput for create.
 */
type InventoryItemFormGroupInput = IInventoryItem | PartialWithRequiredKeyOf<NewInventoryItem>;

type InventoryItemFormDefaults = Pick<NewInventoryItem, 'id' | 'graded'>;

type InventoryItemFormGroupContent = {
  id: FormControl<IInventoryItem['id'] | NewInventoryItem['id']>;
  quantity: FormControl<IInventoryItem['quantity']>;
  condition: FormControl<IInventoryItem['condition']>;
  language: FormControl<IInventoryItem['language']>;
  graded: FormControl<IInventoryItem['graded']>;
  grade: FormControl<IInventoryItem['grade']>;
  purchasePrice: FormControl<IInventoryItem['purchasePrice']>;
  notes: FormControl<IInventoryItem['notes']>;
  card: FormControl<IInventoryItem['card']>;
  owner: FormControl<IInventoryItem['owner']>;
};

export type InventoryItemFormGroup = FormGroup<InventoryItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InventoryItemFormService {
  createInventoryItemFormGroup(inventoryItem: InventoryItemFormGroupInput = { id: null }): InventoryItemFormGroup {
    const inventoryItemRawValue = {
      ...this.getFormDefaults(),
      ...inventoryItem,
    };
    return new FormGroup<InventoryItemFormGroupContent>({
      id: new FormControl(
        { value: inventoryItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(inventoryItemRawValue.quantity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      condition: new FormControl(inventoryItemRawValue.condition, {
        validators: [Validators.required],
      }),
      language: new FormControl(inventoryItemRawValue.language, {
        validators: [Validators.required],
      }),
      graded: new FormControl(inventoryItemRawValue.graded, {
        validators: [Validators.required],
      }),
      grade: new FormControl(inventoryItemRawValue.grade),
      purchasePrice: new FormControl(inventoryItemRawValue.purchasePrice),
      notes: new FormControl(inventoryItemRawValue.notes),
      card: new FormControl(inventoryItemRawValue.card),
      owner: new FormControl(inventoryItemRawValue.owner),
    });
  }

  getInventoryItem(form: InventoryItemFormGroup): IInventoryItem | NewInventoryItem {
    return form.getRawValue() as IInventoryItem | NewInventoryItem;
  }

  resetForm(form: InventoryItemFormGroup, inventoryItem: InventoryItemFormGroupInput): void {
    const inventoryItemRawValue = { ...this.getFormDefaults(), ...inventoryItem };
    form.reset(
      {
        ...inventoryItemRawValue,
        id: { value: inventoryItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InventoryItemFormDefaults {
    return {
      id: null,
      graded: false,
    };
  }
}
