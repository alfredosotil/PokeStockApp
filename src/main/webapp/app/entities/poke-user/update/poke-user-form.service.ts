import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPokeUser, NewPokeUser } from '../poke-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPokeUser for edit and NewPokeUserFormGroupInput for create.
 */
type PokeUserFormGroupInput = IPokeUser | PartialWithRequiredKeyOf<NewPokeUser>;

type PokeUserFormDefaults = Pick<NewPokeUser, 'id'>;

type PokeUserFormGroupContent = {
  id: FormControl<IPokeUser['id'] | NewPokeUser['id']>;
  displayName: FormControl<IPokeUser['displayName']>;
  country: FormControl<IPokeUser['country']>;
  user: FormControl<IPokeUser['user']>;
};

export type PokeUserFormGroup = FormGroup<PokeUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PokeUserFormService {
  createPokeUserFormGroup(pokeUser: PokeUserFormGroupInput = { id: null }): PokeUserFormGroup {
    const pokeUserRawValue = {
      ...this.getFormDefaults(),
      ...pokeUser,
    };
    return new FormGroup<PokeUserFormGroupContent>({
      id: new FormControl(
        { value: pokeUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      displayName: new FormControl(pokeUserRawValue.displayName, {
        validators: [Validators.required],
      }),
      country: new FormControl(pokeUserRawValue.country),
      user: new FormControl(pokeUserRawValue.user),
    });
  }

  getPokeUser(form: PokeUserFormGroup): IPokeUser | NewPokeUser {
    return form.getRawValue() as IPokeUser | NewPokeUser;
  }

  resetForm(form: PokeUserFormGroup, pokeUser: PokeUserFormGroupInput): void {
    const pokeUserRawValue = { ...this.getFormDefaults(), ...pokeUser };
    form.reset(
      {
        ...pokeUserRawValue,
        id: { value: pokeUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PokeUserFormDefaults {
    return {
      id: null,
    };
  }
}
