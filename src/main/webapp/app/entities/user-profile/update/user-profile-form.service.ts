import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserProfile, NewUserProfile } from '../user-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserProfile for edit and NewUserProfileFormGroupInput for create.
 */
type UserProfileFormGroupInput = IUserProfile | PartialWithRequiredKeyOf<NewUserProfile>;

type UserProfileFormDefaults = Pick<NewUserProfile, 'id'>;

type UserProfileFormGroupContent = {
  id: FormControl<IUserProfile['id'] | NewUserProfile['id']>;
  bio: FormControl<IUserProfile['bio']>;
  location: FormControl<IUserProfile['location']>;
  favoriteSet: FormControl<IUserProfile['favoriteSet']>;
  playstyle: FormControl<IUserProfile['playstyle']>;
  avatar: FormControl<IUserProfile['avatar']>;
  avatarContentType: FormControl<IUserProfile['avatarContentType']>;
  user: FormControl<IUserProfile['user']>;
};

export type UserProfileFormGroup = FormGroup<UserProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserProfileFormService {
  createUserProfileFormGroup(userProfile: UserProfileFormGroupInput = { id: null }): UserProfileFormGroup {
    const userProfileRawValue = {
      ...this.getFormDefaults(),
      ...userProfile,
    };
    return new FormGroup<UserProfileFormGroupContent>({
      id: new FormControl(
        { value: userProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      bio: new FormControl(userProfileRawValue.bio, {
        validators: [Validators.maxLength(1000)],
      }),
      location: new FormControl(userProfileRawValue.location),
      favoriteSet: new FormControl(userProfileRawValue.favoriteSet),
      playstyle: new FormControl(userProfileRawValue.playstyle),
      avatar: new FormControl(userProfileRawValue.avatar),
      avatarContentType: new FormControl(userProfileRawValue.avatarContentType),
      user: new FormControl(userProfileRawValue.user),
    });
  }

  getUserProfile(form: UserProfileFormGroup): IUserProfile | NewUserProfile {
    return form.getRawValue() as IUserProfile | NewUserProfile;
  }

  resetForm(form: UserProfileFormGroup, userProfile: UserProfileFormGroupInput): void {
    const userProfileRawValue = { ...this.getFormDefaults(), ...userProfile };
    form.reset(
      {
        ...userProfileRawValue,
        id: { value: userProfileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserProfileFormDefaults {
    return {
      id: null,
    };
  }
}
