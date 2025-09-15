import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CardSetService } from '../service/card-set.service';
import { ICardSet } from '../card-set.model';
import { CardSetFormService } from './card-set-form.service';

import { CardSetUpdateComponent } from './card-set-update.component';

describe('CardSet Management Update Component', () => {
  let comp: CardSetUpdateComponent;
  let fixture: ComponentFixture<CardSetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cardSetFormService: CardSetFormService;
  let cardSetService: CardSetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CardSetUpdateComponent],
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
      .overrideTemplate(CardSetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardSetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cardSetFormService = TestBed.inject(CardSetFormService);
    cardSetService = TestBed.inject(CardSetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const cardSet: ICardSet = { id: 22016 };

      activatedRoute.data = of({ cardSet });
      comp.ngOnInit();

      expect(comp.cardSet).toEqual(cardSet);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardSet>>();
      const cardSet = { id: 12456 };
      jest.spyOn(cardSetFormService, 'getCardSet').mockReturnValue(cardSet);
      jest.spyOn(cardSetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardSet }));
      saveSubject.complete();

      // THEN
      expect(cardSetFormService.getCardSet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cardSetService.update).toHaveBeenCalledWith(expect.objectContaining(cardSet));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardSet>>();
      const cardSet = { id: 12456 };
      jest.spyOn(cardSetFormService, 'getCardSet').mockReturnValue({ id: null });
      jest.spyOn(cardSetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardSet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardSet }));
      saveSubject.complete();

      // THEN
      expect(cardSetFormService.getCardSet).toHaveBeenCalled();
      expect(cardSetService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardSet>>();
      const cardSet = { id: 12456 };
      jest.spyOn(cardSetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cardSetService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
