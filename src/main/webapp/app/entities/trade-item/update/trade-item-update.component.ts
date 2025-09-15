import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITrade } from 'app/entities/trade/trade.model';
import { TradeService } from 'app/entities/trade/service/trade.service';
import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { TradeItemService } from '../service/trade-item.service';
import { ITradeItem } from '../trade-item.model';
import { TradeItemFormGroup, TradeItemFormService } from './trade-item-form.service';

@Component({
  selector: 'jhi-trade-item-update',
  templateUrl: './trade-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TradeItemUpdateComponent implements OnInit {
  isSaving = false;
  tradeItem: ITradeItem | null = null;

  tradesSharedCollection: ITrade[] = [];
  cardsSharedCollection: ICard[] = [];

  protected tradeItemService = inject(TradeItemService);
  protected tradeItemFormService = inject(TradeItemFormService);
  protected tradeService = inject(TradeService);
  protected cardService = inject(CardService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TradeItemFormGroup = this.tradeItemFormService.createTradeItemFormGroup();

  compareTrade = (o1: ITrade | null, o2: ITrade | null): boolean => this.tradeService.compareTrade(o1, o2);

  compareCard = (o1: ICard | null, o2: ICard | null): boolean => this.cardService.compareCard(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tradeItem }) => {
      this.tradeItem = tradeItem;
      if (tradeItem) {
        this.updateForm(tradeItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tradeItem = this.tradeItemFormService.getTradeItem(this.editForm);
    if (tradeItem.id !== null) {
      this.subscribeToSaveResponse(this.tradeItemService.update(tradeItem));
    } else {
      this.subscribeToSaveResponse(this.tradeItemService.create(tradeItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITradeItem>>): void {
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

  protected updateForm(tradeItem: ITradeItem): void {
    this.tradeItem = tradeItem;
    this.tradeItemFormService.resetForm(this.editForm, tradeItem);

    this.tradesSharedCollection = this.tradeService.addTradeToCollectionIfMissing<ITrade>(this.tradesSharedCollection, tradeItem.trade);
    this.cardsSharedCollection = this.cardService.addCardToCollectionIfMissing<ICard>(this.cardsSharedCollection, tradeItem.card);
  }

  protected loadRelationshipsOptions(): void {
    this.tradeService
      .query()
      .pipe(map((res: HttpResponse<ITrade[]>) => res.body ?? []))
      .pipe(map((trades: ITrade[]) => this.tradeService.addTradeToCollectionIfMissing<ITrade>(trades, this.tradeItem?.trade)))
      .subscribe((trades: ITrade[]) => (this.tradesSharedCollection = trades));

    this.cardService
      .query()
      .pipe(map((res: HttpResponse<ICard[]>) => res.body ?? []))
      .pipe(map((cards: ICard[]) => this.cardService.addCardToCollectionIfMissing<ICard>(cards, this.tradeItem?.card)))
      .subscribe((cards: ICard[]) => (this.cardsSharedCollection = cards));
  }
}
