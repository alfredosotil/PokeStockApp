import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITrade } from 'app/entities/trade/trade.model';
import { TradeService } from 'app/entities/trade/service/trade.service';
import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { ITradeItem } from '../trade-item.model';
import { TradeItemService } from '../service/trade-item.service';
import { TradeItemFormService } from './trade-item-form.service';

import { TradeItemUpdateComponent } from './trade-item-update.component';

describe('TradeItem Management Update Component', () => {
  let comp: TradeItemUpdateComponent;
  let fixture: ComponentFixture<TradeItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tradeItemFormService: TradeItemFormService;
  let tradeItemService: TradeItemService;
  let tradeService: TradeService;
  let cardService: CardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TradeItemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TradeItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TradeItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tradeItemFormService = TestBed.inject(TradeItemFormService);
    tradeItemService = TestBed.inject(TradeItemService);
    tradeService = TestBed.inject(TradeService);
    cardService = TestBed.inject(CardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Trade query and add missing value', () => {
      const tradeItem: ITradeItem = { id: 5123 };
      const trade: ITrade = { id: 21063 };
      tradeItem.trade = trade;

      const tradeCollection: ITrade[] = [{ id: 21063 }];
      jest.spyOn(tradeService, 'query').mockReturnValue(of(new HttpResponse({ body: tradeCollection })));
      const additionalTrades = [trade];
      const expectedCollection: ITrade[] = [...additionalTrades, ...tradeCollection];
      jest.spyOn(tradeService, 'addTradeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tradeItem });
      comp.ngOnInit();

      expect(tradeService.query).toHaveBeenCalled();
      expect(tradeService.addTradeToCollectionIfMissing).toHaveBeenCalledWith(
        tradeCollection,
        ...additionalTrades.map(expect.objectContaining),
      );
      expect(comp.tradesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Card query and add missing value', () => {
      const tradeItem: ITradeItem = { id: 5123 };
      const card: ICard = { id: 22516 };
      tradeItem.card = card;

      const cardCollection: ICard[] = [{ id: 22516 }];
      jest.spyOn(cardService, 'query').mockReturnValue(of(new HttpResponse({ body: cardCollection })));
      const additionalCards = [card];
      const expectedCollection: ICard[] = [...additionalCards, ...cardCollection];
      jest.spyOn(cardService, 'addCardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tradeItem });
      comp.ngOnInit();

      expect(cardService.query).toHaveBeenCalled();
      expect(cardService.addCardToCollectionIfMissing).toHaveBeenCalledWith(
        cardCollection,
        ...additionalCards.map(expect.objectContaining),
      );
      expect(comp.cardsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tradeItem: ITradeItem = { id: 5123 };
      const trade: ITrade = { id: 21063 };
      tradeItem.trade = trade;
      const card: ICard = { id: 22516 };
      tradeItem.card = card;

      activatedRoute.data = of({ tradeItem });
      comp.ngOnInit();

      expect(comp.tradesSharedCollection).toContainEqual(trade);
      expect(comp.cardsSharedCollection).toContainEqual(card);
      expect(comp.tradeItem).toEqual(tradeItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITradeItem>>();
      const tradeItem = { id: 25905 };
      jest.spyOn(tradeItemFormService, 'getTradeItem').mockReturnValue(tradeItem);
      jest.spyOn(tradeItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tradeItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tradeItem }));
      saveSubject.complete();

      // THEN
      expect(tradeItemFormService.getTradeItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tradeItemService.update).toHaveBeenCalledWith(expect.objectContaining(tradeItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITradeItem>>();
      const tradeItem = { id: 25905 };
      jest.spyOn(tradeItemFormService, 'getTradeItem').mockReturnValue({ id: null });
      jest.spyOn(tradeItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tradeItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tradeItem }));
      saveSubject.complete();

      // THEN
      expect(tradeItemFormService.getTradeItem).toHaveBeenCalled();
      expect(tradeItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITradeItem>>();
      const tradeItem = { id: 25905 };
      jest.spyOn(tradeItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tradeItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tradeItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTrade', () => {
      it('should forward to tradeService', () => {
        const entity = { id: 21063 };
        const entity2 = { id: 30501 };
        jest.spyOn(tradeService, 'compareTrade');
        comp.compareTrade(entity, entity2);
        expect(tradeService.compareTrade).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCard', () => {
      it('should forward to cardService', () => {
        const entity = { id: 22516 };
        const entity2 = { id: 1984 };
        jest.spyOn(cardService, 'compareCard');
        comp.compareCard(entity, entity2);
        expect(cardService.compareCard).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
