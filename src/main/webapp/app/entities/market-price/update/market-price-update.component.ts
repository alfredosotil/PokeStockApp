import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { MarketSource } from 'app/entities/enumerations/market-source.model';
import { MarketPriceService } from '../service/market-price.service';
import { IMarketPrice } from '../market-price.model';
import { MarketPriceFormGroup, MarketPriceFormService } from './market-price-form.service';

@Component({
  selector: 'jhi-market-price-update',
  templateUrl: './market-price-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MarketPriceUpdateComponent implements OnInit {
  isSaving = false;
  marketPrice: IMarketPrice | null = null;
  marketSourceValues = Object.keys(MarketSource);

  cardsSharedCollection: ICard[] = [];

  protected marketPriceService = inject(MarketPriceService);
  protected marketPriceFormService = inject(MarketPriceFormService);
  protected cardService = inject(CardService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MarketPriceFormGroup = this.marketPriceFormService.createMarketPriceFormGroup();

  compareCard = (o1: ICard | null, o2: ICard | null): boolean => this.cardService.compareCard(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ marketPrice }) => {
      this.marketPrice = marketPrice;
      if (marketPrice) {
        this.updateForm(marketPrice);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const marketPrice = this.marketPriceFormService.getMarketPrice(this.editForm);
    if (marketPrice.id !== null) {
      this.subscribeToSaveResponse(this.marketPriceService.update(marketPrice));
    } else {
      this.subscribeToSaveResponse(this.marketPriceService.create(marketPrice));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMarketPrice>>): void {
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

  protected updateForm(marketPrice: IMarketPrice): void {
    this.marketPrice = marketPrice;
    this.marketPriceFormService.resetForm(this.editForm, marketPrice);

    this.cardsSharedCollection = this.cardService.addCardToCollectionIfMissing<ICard>(this.cardsSharedCollection, marketPrice.card);
  }

  protected loadRelationshipsOptions(): void {
    this.cardService
      .query()
      .pipe(map((res: HttpResponse<ICard[]>) => res.body ?? []))
      .pipe(map((cards: ICard[]) => this.cardService.addCardToCollectionIfMissing<ICard>(cards, this.marketPrice?.card)))
      .subscribe((cards: ICard[]) => (this.cardsSharedCollection = cards));
  }
}
