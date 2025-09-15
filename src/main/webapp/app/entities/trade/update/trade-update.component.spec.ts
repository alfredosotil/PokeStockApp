import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPokeUser } from 'app/entities/poke-user/poke-user.model';
import { PokeUserService } from 'app/entities/poke-user/service/poke-user.service';
import { TradeService } from '../service/trade.service';
import { ITrade } from '../trade.model';
import { TradeFormService } from './trade-form.service';

import { TradeUpdateComponent } from './trade-update.component';

describe('Trade Management Update Component', () => {
  let comp: TradeUpdateComponent;
  let fixture: ComponentFixture<TradeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tradeFormService: TradeFormService;
  let tradeService: TradeService;
  let pokeUserService: PokeUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TradeUpdateComponent],
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
      .overrideTemplate(TradeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TradeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tradeFormService = TestBed.inject(TradeFormService);
    tradeService = TestBed.inject(TradeService);
    pokeUserService = TestBed.inject(PokeUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PokeUser query and add missing value', () => {
      const trade: ITrade = { id: 30501 };
      const proposer: IPokeUser = { id: 15664 };
      trade.proposer = proposer;

      const pokeUserCollection: IPokeUser[] = [{ id: 15664 }];
      jest.spyOn(pokeUserService, 'query').mockReturnValue(of(new HttpResponse({ body: pokeUserCollection })));
      const additionalPokeUsers = [proposer];
      const expectedCollection: IPokeUser[] = [...additionalPokeUsers, ...pokeUserCollection];
      jest.spyOn(pokeUserService, 'addPokeUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trade });
      comp.ngOnInit();

      expect(pokeUserService.query).toHaveBeenCalled();
      expect(pokeUserService.addPokeUserToCollectionIfMissing).toHaveBeenCalledWith(
        pokeUserCollection,
        ...additionalPokeUsers.map(expect.objectContaining),
      );
      expect(comp.pokeUsersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const trade: ITrade = { id: 30501 };
      const proposer: IPokeUser = { id: 15664 };
      trade.proposer = proposer;

      activatedRoute.data = of({ trade });
      comp.ngOnInit();

      expect(comp.pokeUsersSharedCollection).toContainEqual(proposer);
      expect(comp.trade).toEqual(trade);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrade>>();
      const trade = { id: 21063 };
      jest.spyOn(tradeFormService, 'getTrade').mockReturnValue(trade);
      jest.spyOn(tradeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trade });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trade }));
      saveSubject.complete();

      // THEN
      expect(tradeFormService.getTrade).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tradeService.update).toHaveBeenCalledWith(expect.objectContaining(trade));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrade>>();
      const trade = { id: 21063 };
      jest.spyOn(tradeFormService, 'getTrade').mockReturnValue({ id: null });
      jest.spyOn(tradeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trade: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trade }));
      saveSubject.complete();

      // THEN
      expect(tradeFormService.getTrade).toHaveBeenCalled();
      expect(tradeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrade>>();
      const trade = { id: 21063 };
      jest.spyOn(tradeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trade });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tradeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
