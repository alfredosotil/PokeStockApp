import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICardSet } from 'app/entities/card-set/card-set.model';
import { CardSetService } from 'app/entities/card-set/service/card-set.service';
import { ICard } from '../card.model';
import { CardService } from '../service/card.service';
import { CardFormGroup, CardFormService } from './card-form.service';

@Component({
  selector: 'jhi-card-update',
  templateUrl: './card-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CardUpdateComponent implements OnInit {
  isSaving = false;
  card: ICard | null = null;

  cardSetsSharedCollection: ICardSet[] = [];

  protected cardService = inject(CardService);
  protected cardFormService = inject(CardFormService);
  protected cardSetService = inject(CardSetService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CardFormGroup = this.cardFormService.createCardFormGroup();

  compareCardSet = (o1: ICardSet | null, o2: ICardSet | null): boolean => this.cardSetService.compareCardSet(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ card }) => {
      this.card = card;
      if (card) {
        this.updateForm(card);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const card = this.cardFormService.getCard(this.editForm);
    if (card.id !== null) {
      this.subscribeToSaveResponse(this.cardService.update(card));
    } else {
      this.subscribeToSaveResponse(this.cardService.create(card));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICard>>): void {
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

  protected updateForm(card: ICard): void {
    this.card = card;
    this.cardFormService.resetForm(this.editForm, card);

    this.cardSetsSharedCollection = this.cardSetService.addCardSetToCollectionIfMissing<ICardSet>(this.cardSetsSharedCollection, card.set);
  }

  protected loadRelationshipsOptions(): void {
    this.cardSetService
      .query()
      .pipe(map((res: HttpResponse<ICardSet[]>) => res.body ?? []))
      .pipe(map((cardSets: ICardSet[]) => this.cardSetService.addCardSetToCollectionIfMissing<ICardSet>(cardSets, this.card?.set)))
      .subscribe((cardSets: ICardSet[]) => (this.cardSetsSharedCollection = cardSets));
  }
}
