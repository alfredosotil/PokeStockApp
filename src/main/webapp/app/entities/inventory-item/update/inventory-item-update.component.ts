import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { PokeUserService } from 'app/entities/poke-user/service/poke-user.service';
import { CardCondition } from 'app/entities/enumerations/card-condition.model';
import { CardLanguage } from 'app/entities/enumerations/card-language.model';
import { InventoryItemService } from '../service/inventory-item.service';
import { IInventoryItem } from '../inventory-item.model';
import { InventoryItemFormGroup, InventoryItemFormService } from './inventory-item-form.service';

@Component({
  selector: 'jhi-inventory-item-update',
  templateUrl: './inventory-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InventoryItemUpdateComponent implements OnInit {
  isSaving = false;
  inventoryItem: IInventoryItem | null = null;
  cardConditionValues = Object.keys(CardCondition);
  cardLanguageValues = Object.keys(CardLanguage);

  cardsSharedCollection: ICard[] = [];
  pokeUsersSharedCollection: IPokeUser[] = [];

  protected inventoryItemService = inject(InventoryItemService);
  protected inventoryItemFormService = inject(InventoryItemFormService);
  protected cardService = inject(CardService);
  protected pokeUserService = inject(PokeUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InventoryItemFormGroup = this.inventoryItemFormService.createInventoryItemFormGroup();

  compareCard = (o1: ICard | null, o2: ICard | null): boolean => this.cardService.compareCard(o1, o2);

  comparePokeUser = (o1: IPokeUser | null, o2: IPokeUser | null): boolean => this.pokeUserService.comparePokeUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventoryItem }) => {
      this.inventoryItem = inventoryItem;
      if (inventoryItem) {
        this.updateForm(inventoryItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inventoryItem = this.inventoryItemFormService.getInventoryItem(this.editForm);
    if (inventoryItem.id !== null) {
      this.subscribeToSaveResponse(this.inventoryItemService.update(inventoryItem));
    } else {
      this.subscribeToSaveResponse(this.inventoryItemService.create(inventoryItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInventoryItem>>): void {
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

  protected updateForm(inventoryItem: IInventoryItem): void {
    this.inventoryItem = inventoryItem;
    this.inventoryItemFormService.resetForm(this.editForm, inventoryItem);

    this.cardsSharedCollection = this.cardService.addCardToCollectionIfMissing<ICard>(this.cardsSharedCollection, inventoryItem.card);
    this.pokeUsersSharedCollection = this.pokeUserService.addPokeUserToCollectionIfMissing<IPokeUser>(
      this.pokeUsersSharedCollection,
      inventoryItem.owner,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cardService
      .query()
      .pipe(map((res: HttpResponse<ICard[]>) => res.body ?? []))
      .pipe(map((cards: ICard[]) => this.cardService.addCardToCollectionIfMissing<ICard>(cards, this.inventoryItem?.card)))
      .subscribe((cards: ICard[]) => (this.cardsSharedCollection = cards));

    this.pokeUserService
      .query()
      .pipe(map((res: HttpResponse<IPokeUser[]>) => res.body ?? []))
      .pipe(
        map((pokeUsers: IPokeUser[]) =>
          this.pokeUserService.addPokeUserToCollectionIfMissing<IPokeUser>(pokeUsers, this.inventoryItem?.owner),
        ),
      )
      .subscribe((pokeUsers: IPokeUser[]) => (this.pokeUsersSharedCollection = pokeUsers));
  }
}
