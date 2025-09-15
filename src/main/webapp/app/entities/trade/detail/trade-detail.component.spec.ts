import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TradeDetailComponent } from './trade-detail.component';

describe('Trade Management Detail Component', () => {
  let comp: TradeDetailComponent;
  let fixture: ComponentFixture<TradeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TradeDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./trade-detail.component').then(m => m.TradeDetailComponent),
              resolve: { trade: () => of({ id: 21063 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TradeDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TradeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load trade on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TradeDetailComponent);

      // THEN
      expect(instance.trade()).toEqual(expect.objectContaining({ id: 21063 }));
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
