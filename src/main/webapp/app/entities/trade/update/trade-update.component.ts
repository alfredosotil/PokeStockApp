import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { PokeUserService } from 'app/entities/poke-user/service/poke-user.service';
import { TradeStatus } from 'app/entities/enumerations/trade-status.model';
import { TradeService } from '../service/trade.service';
import { ITrade } from '../trade.model';
import { TradeFormGroup, TradeFormService } from './trade-form.service';

@Component({
  selector: 'jhi-trade-update',
  templateUrl: './trade-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TradeUpdateComponent implements OnInit {
  isSaving = false;
  trade: ITrade | null = null;
  tradeStatusValues = Object.keys(TradeStatus);

  pokeUsersSharedCollection: IPokeUser[] = [];

  protected tradeService = inject(TradeService);
  protected tradeFormService = inject(TradeFormService);
  protected pokeUserService = inject(PokeUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TradeFormGroup = this.tradeFormService.createTradeFormGroup();

  comparePokeUser = (o1: IPokeUser | null, o2: IPokeUser | null): boolean => this.pokeUserService.comparePokeUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trade }) => {
      this.trade = trade;
      if (trade) {
        this.updateForm(trade);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trade = this.tradeFormService.getTrade(this.editForm);
    if (trade.id !== null) {
      this.subscribeToSaveResponse(this.tradeService.update(trade));
    } else {
      this.subscribeToSaveResponse(this.tradeService.create(trade));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrade>>): void {
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

  protected updateForm(trade: ITrade): void {
    this.trade = trade;
    this.tradeFormService.resetForm(this.editForm, trade);

    this.pokeUsersSharedCollection = this.pokeUserService.addPokeUserToCollectionIfMissing<IPokeUser>(
      this.pokeUsersSharedCollection,
      trade.proposer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.pokeUserService
      .query()
      .pipe(map((res: HttpResponse<IPokeUser[]>) => res.body ?? []))
      .pipe(
        map((pokeUsers: IPokeUser[]) => this.pokeUserService.addPokeUserToCollectionIfMissing<IPokeUser>(pokeUsers, this.trade?.proposer)),
      )
      .subscribe((pokeUsers: IPokeUser[]) => (this.pokeUsersSharedCollection = pokeUsers));
  }
}
