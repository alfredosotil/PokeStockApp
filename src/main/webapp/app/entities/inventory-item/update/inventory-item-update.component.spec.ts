import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';
import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { PokeUserService } from 'app/entities/poke-user/service/poke-user.service';
import { IInventoryItem } from '../inventory-item.model';
import { InventoryItemService } from '../service/inventory-item.service';
import { InventoryItemFormService } from './inventory-item-form.service';

import { InventoryItemUpdateComponent } from './inventory-item-update.component';

describe('InventoryItem Management Update Component', () => {
  let comp: InventoryItemUpdateComponent;
  let fixture: ComponentFixture<InventoryItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inventoryItemFormService: InventoryItemFormService;
  let inventoryItemService: InventoryItemService;
  let cardService: CardService;
  let pokeUserService: PokeUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InventoryItemUpdateComponent],
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
      .overrideTemplate(InventoryItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InventoryItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inventoryItemFormService = TestBed.inject(InventoryItemFormService);
    inventoryItemService = TestBed.inject(InventoryItemService);
    cardService = TestBed.inject(CardService);
    pokeUserService = TestBed.inject(PokeUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Card query and add missing value', () => {
      const inventoryItem: IInventoryItem = { id: 4332 };
      const card: ICard = { id: 22516 };
      inventoryItem.card = card;

      const cardCollection: ICard[] = [{ id: 22516 }];
      jest.spyOn(cardService, 'query').mockReturnValue(of(new HttpResponse({ body: cardCollection })));
      const additionalCards = [card];
      const expectedCollection: ICard[] = [...additionalCards, ...cardCollection];
      jest.spyOn(cardService, 'addCardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventoryItem });
      comp.ngOnInit();

      expect(cardService.query).toHaveBeenCalled();
      expect(cardService.addCardToCollectionIfMissing).toHaveBeenCalledWith(
        cardCollection,
        ...additionalCards.map(expect.objectContaining),
      );
      expect(comp.cardsSharedCollection).toEqual(expectedCollection);
    });

    it('should call PokeUser query and add missing value', () => {
      const inventoryItem: IInventoryItem = { id: 4332 };
      const owner: IPokeUser = { id: 15664 };
      inventoryItem.owner = owner;

      const pokeUserCollection: IPokeUser[] = [{ id: 15664 }];
      jest.spyOn(pokeUserService, 'query').mockReturnValue(of(new HttpResponse({ body: pokeUserCollection })));
      const additionalPokeUsers = [owner];
      const expectedCollection: IPokeUser[] = [...additionalPokeUsers, ...pokeUserCollection];
      jest.spyOn(pokeUserService, 'addPokeUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventoryItem });
      comp.ngOnInit();

      expect(pokeUserService.query).toHaveBeenCalled();
      expect(pokeUserService.addPokeUserToCollectionIfMissing).toHaveBeenCalledWith(
        pokeUserCollection,
        ...additionalPokeUsers.map(expect.objectContaining),
      );
      expect(comp.pokeUsersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const inventoryItem: IInventoryItem = { id: 4332 };
      const card: ICard = { id: 22516 };
      inventoryItem.card = card;
      const owner: IPokeUser = { id: 15664 };
      inventoryItem.owner = owner;

      activatedRoute.data = of({ inventoryItem });
      comp.ngOnInit();

      expect(comp.cardsSharedCollection).toContainEqual(card);
      expect(comp.pokeUsersSharedCollection).toContainEqual(owner);
      expect(comp.inventoryItem).toEqual(inventoryItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryItem>>();
      const inventoryItem = { id: 7462 };
      jest.spyOn(inventoryItemFormService, 'getInventoryItem').mockReturnValue(inventoryItem);
      jest.spyOn(inventoryItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventoryItem }));
      saveSubject.complete();

      // THEN
      expect(inventoryItemFormService.getInventoryItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inventoryItemService.update).toHaveBeenCalledWith(expect.objectContaining(inventoryItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryItem>>();
      const inventoryItem = { id: 7462 };
      jest.spyOn(inventoryItemFormService, 'getInventoryItem').mockReturnValue({ id: null });
      jest.spyOn(inventoryItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventoryItem }));
      saveSubject.complete();

      // THEN
      expect(inventoryItemFormService.getInventoryItem).toHaveBeenCalled();
      expect(inventoryItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryItem>>();
      const inventoryItem = { id: 7462 };
      jest.spyOn(inventoryItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inventoryItemService.update).toHaveBeenCalled();
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

    describe('comparePokeUser', () => {
      it('should forward to pokeUserService', () => {
        const entity = { id: 15664 };
        const entity2 = { id: 27644 };
        jest.spyOn(pokeUserService, 'comparePokeUser');
        comp.comparePokeUser(entity, entity2);
        expect(pokeUserService.comparePokeUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
