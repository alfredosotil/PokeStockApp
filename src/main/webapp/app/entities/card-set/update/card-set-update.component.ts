import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICardSet } from '../card-set.model';
import { CardSetService } from '../service/card-set.service';
import { CardSetFormGroup, CardSetFormService } from './card-set-form.service';

@Component({
  selector: 'jhi-card-set-update',
  templateUrl: './card-set-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CardSetUpdateComponent implements OnInit {
  isSaving = false;
  cardSet: ICardSet | null = null;

  protected cardSetService = inject(CardSetService);
  protected cardSetFormService = inject(CardSetFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CardSetFormGroup = this.cardSetFormService.createCardSetFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cardSet }) => {
      this.cardSet = cardSet;
      if (cardSet) {
        this.updateForm(cardSet);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cardSet = this.cardSetFormService.getCardSet(this.editForm);
    if (cardSet.id !== null) {
      this.subscribeToSaveResponse(this.cardSetService.update(cardSet));
    } else {
      this.subscribeToSaveResponse(this.cardSetService.create(cardSet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICardSet>>): void {
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

  protected updateForm(cardSet: ICardSet): void {
    this.cardSet = cardSet;
    this.cardSetFormService.resetForm(this.editForm, cardSet);
  }
}
