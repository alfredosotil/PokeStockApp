import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPokeUser } from '../poke-user.model';
import { PokeUserService } from '../service/poke-user.service';
import { PokeUserFormGroup, PokeUserFormService } from './poke-user-form.service';

@Component({
  selector: 'jhi-poke-user-update',
  templateUrl: './poke-user-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PokeUserUpdateComponent implements OnInit {
  isSaving = false;
  pokeUser: IPokeUser | null = null;

  usersSharedCollection: IUser[] = [];

  protected pokeUserService = inject(PokeUserService);
  protected pokeUserFormService = inject(PokeUserFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PokeUserFormGroup = this.pokeUserFormService.createPokeUserFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pokeUser }) => {
      this.pokeUser = pokeUser;
      if (pokeUser) {
        this.updateForm(pokeUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pokeUser = this.pokeUserFormService.getPokeUser(this.editForm);
    if (pokeUser.id !== null) {
      this.subscribeToSaveResponse(this.pokeUserService.update(pokeUser));
    } else {
      this.subscribeToSaveResponse(this.pokeUserService.create(pokeUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPokeUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(pokeUser: IPokeUser): void {
    this.pokeUser = pokeUser;
    this.pokeUserFormService.resetForm(this.editForm, pokeUser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, pokeUser.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.pokeUser?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
