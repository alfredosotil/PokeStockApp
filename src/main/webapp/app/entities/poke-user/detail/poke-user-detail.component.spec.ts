import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PokeUserDetailComponent } from './poke-user-detail.component';

describe('PokeUser Management Detail Component', () => {
  let comp: PokeUserDetailComponent;
  let fixture: ComponentFixture<PokeUserDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PokeUserDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./poke-user-detail.component').then(m => m.PokeUserDetailComponent),
              resolve: { pokeUser: () => of({ id: 15664 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PokeUserDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PokeUserDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pokeUser on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PokeUserDetailComponent);

      // THEN
      expect(instance.pokeUser()).toEqual(expect.objectContaining({ id: 15664 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
