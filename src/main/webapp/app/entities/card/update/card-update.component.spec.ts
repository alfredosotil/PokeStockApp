import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICardSet } from 'app/entities/card-set/card-set.model';
import { CardSetService } from 'app/entities/card-set/service/card-set.service';
import { CardService } from '../service/card.service';
import { ICard } from '../card.model';
import { CardFormService } from './card-form.service';

import { CardUpdateComponent } from './card-update.component';

describe('Card Management Update Component', () => {
  let comp: CardUpdateComponent;
  let fixture: ComponentFixture<CardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cardFormService: CardFormService;
  let cardService: CardService;
  let cardSetService: CardSetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CardUpdateComponent],
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
      .overrideTemplate(CardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cardFormService = TestBed.inject(CardFormService);
    cardService = TestBed.inject(CardService);
    cardSetService = TestBed.inject(CardSetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CardSet query and add missing value', () => {
      const card: ICard = { id: 1984 };
      const set: ICardSet = { id: 12456 };
      card.set = set;

      const cardSetCollection: ICardSet[] = [{ id: 12456 }];
      jest.spyOn(cardSetService, 'query').mockReturnValue(of(new HttpResponse({ body: cardSetCollection })));
      const additionalCardSets = [set];
      const expectedCollection: ICardSet[] = [...additionalCardSets, ...cardSetCollection];
      jest.spyOn(cardSetService, 'addCardSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ card });
      comp.ngOnInit();

      expect(cardSetService.query).toHaveBeenCalled();
      expect(cardSetService.addCardSetToCollectionIfMissing).toHaveBeenCalledWith(
        cardSetCollection,
        ...additionalCardSets.map(expect.objectContaining),
      );
      expect(comp.cardSetsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const card: ICard = { id: 1984 };
      const set: ICardSet = { id: 12456 };
      card.set = set;

      activatedRoute.data = of({ card });
      comp.ngOnInit();

      expect(comp.cardSetsSharedCollection).toContainEqual(set);
      expect(comp.card).toEqual(card);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 22516 };
      jest.spyOn(cardFormService, 'getCard').mockReturnValue(card);
      jest.spyOn(cardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: card }));
      saveSubject.complete();

      // THEN
      expect(cardFormService.getCard).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cardService.update).toHaveBeenCalledWith(expect.objectContaining(card));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 22516 };
      jest.spyOn(cardFormService, 'getCard').mockReturnValue({ id: null });
      jest.spyOn(cardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: card }));
      saveSubject.complete();

      // THEN
      expect(cardFormService.getCard).toHaveBeenCalled();
      expect(cardService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 22516 };
      jest.spyOn(cardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cardService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCardSet', () => {
      it('should forward to cardSetService', () => {
        const entity = { id: 12456 };
        const entity2 = { id: 22016 };
        jest.spyOn(cardSetService, 'compareCardSet');
        comp.compareCardSet(entity, entity2);
        expect(cardSetService.compareCardSet).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
