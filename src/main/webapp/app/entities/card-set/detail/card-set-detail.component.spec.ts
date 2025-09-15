import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CardSetDetailComponent } from './card-set-detail.component';

describe('CardSet Management Detail Component', () => {
  let comp: CardSetDetailComponent;
  let fixture: ComponentFixture<CardSetDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardSetDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./card-set-detail.component').then(m => m.CardSetDetailComponent),
              resolve: { cardSet: () => of({ id: 12456 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CardSetDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardSetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cardSet on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CardSetDetailComponent);

      // THEN
      expect(instance.cardSet()).toEqual(expect.objectContaining({ id: 12456 }));
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
