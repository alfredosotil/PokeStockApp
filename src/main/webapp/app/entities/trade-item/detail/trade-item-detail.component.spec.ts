import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TradeItemDetailComponent } from './trade-item-detail.component';

describe('TradeItem Management Detail Component', () => {
  let comp: TradeItemDetailComponent;
  let fixture: ComponentFixture<TradeItemDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TradeItemDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./trade-item-detail.component').then(m => m.TradeItemDetailComponent),
              resolve: { tradeItem: () => of({ id: 25905 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TradeItemDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TradeItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load tradeItem on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TradeItemDetailComponent);

      // THEN
      expect(instance.tradeItem()).toEqual(expect.objectContaining({ id: 25905 }));
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
