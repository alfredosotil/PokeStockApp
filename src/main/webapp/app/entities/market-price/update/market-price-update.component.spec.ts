import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { MarketPriceService } from '../service/market-price.service';
import { IMarketPrice } from '../market-price.model';
import { MarketPriceFormService } from './market-price-form.service';

import { MarketPriceUpdateComponent } from './market-price-update.component';

describe('MarketPrice Management Update Component', () => {
  let comp: MarketPriceUpdateComponent;
  let fixture: ComponentFixture<MarketPriceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let marketPriceFormService: MarketPriceFormService;
  let marketPriceService: MarketPriceService;
  let cardService: CardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MarketPriceUpdateComponent],
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
      .overrideTemplate(MarketPriceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MarketPriceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    marketPriceFormService = TestBed.inject(MarketPriceFormService);
    marketPriceService = TestBed.inject(MarketPriceService);
    cardService = TestBed.inject(CardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Card query and add missing value', () => {
      const marketPrice: IMarketPrice = { id: 26418 };
      const card: ICard = { id: 22516 };
      marketPrice.card = card;

      const cardCollection: ICard[] = [{ id: 22516 }];
      jest.spyOn(cardService, 'query').mockReturnValue(of(new HttpResponse({ body: cardCollection })));
      const additionalCards = [card];
      const expectedCollection: ICard[] = [...additionalCards, ...cardCollection];
      jest.spyOn(cardService, 'addCardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ marketPrice });
      comp.ngOnInit();

      expect(cardService.query).toHaveBeenCalled();
      expect(cardService.addCardToCollectionIfMissing).toHaveBeenCalledWith(
        cardCollection,
        ...additionalCards.map(expect.objectContaining),
      );
      expect(comp.cardsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const marketPrice: IMarketPrice = { id: 26418 };
      const card: ICard = { id: 22516 };
      marketPrice.card = card;

      activatedRoute.data = of({ marketPrice });
      comp.ngOnInit();

      expect(comp.cardsSharedCollection).toContainEqual(card);
      expect(comp.marketPrice).toEqual(marketPrice);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMarketPrice>>();
      const marketPrice = { id: 20939 };
      jest.spyOn(marketPriceFormService, 'getMarketPrice').mockReturnValue(marketPrice);
      jest.spyOn(marketPriceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ marketPrice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: marketPrice }));
      saveSubject.complete();

      // THEN
      expect(marketPriceFormService.getMarketPrice).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(marketPriceService.update).toHaveBeenCalledWith(expect.objectContaining(marketPrice));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMarketPrice>>();
      const marketPrice = { id: 20939 };
      jest.spyOn(marketPriceFormService, 'getMarketPrice').mockReturnValue({ id: null });
      jest.spyOn(marketPriceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ marketPrice: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: marketPrice }));
      saveSubject.complete();

      // THEN
      expect(marketPriceFormService.getMarketPrice).toHaveBeenCalled();
      expect(marketPriceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMarketPrice>>();
      const marketPrice = { id: 20939 };
      jest.spyOn(marketPriceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ marketPrice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(marketPriceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
