import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { PokeUserService } from '../service/poke-user.service';
import { IPokeUser } from '../poke-user.model';
import { PokeUserFormService } from './poke-user-form.service';

import { PokeUserUpdateComponent } from './poke-user-update.component';

describe('PokeUser Management Update Component', () => {
  let comp: PokeUserUpdateComponent;
  let fixture: ComponentFixture<PokeUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pokeUserFormService: PokeUserFormService;
  let pokeUserService: PokeUserService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PokeUserUpdateComponent],
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
      .overrideTemplate(PokeUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PokeUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pokeUserFormService = TestBed.inject(PokeUserFormService);
    pokeUserService = TestBed.inject(PokeUserService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const pokeUser: IPokeUser = { id: 27644 };
      const user: IUser = { id: 3944 };
      pokeUser.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pokeUser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pokeUser: IPokeUser = { id: 27644 };
      const user: IUser = { id: 3944 };
      pokeUser.user = user;

      activatedRoute.data = of({ pokeUser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.pokeUser).toEqual(pokeUser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPokeUser>>();
      const pokeUser = { id: 15664 };
      jest.spyOn(pokeUserFormService, 'getPokeUser').mockReturnValue(pokeUser);
      jest.spyOn(pokeUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pokeUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pokeUser }));
      saveSubject.complete();

      // THEN
      expect(pokeUserFormService.getPokeUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pokeUserService.update).toHaveBeenCalledWith(expect.objectContaining(pokeUser));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPokeUser>>();
      const pokeUser = { id: 15664 };
      jest.spyOn(pokeUserFormService, 'getPokeUser').mockReturnValue({ id: null });
      jest.spyOn(pokeUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pokeUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pokeUser }));
      saveSubject.complete();

      // THEN
      expect(pokeUserFormService.getPokeUser).toHaveBeenCalled();
      expect(pokeUserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPokeUser>>();
      const pokeUser = { id: 15664 };
      jest.spyOn(pokeUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pokeUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pokeUserService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
