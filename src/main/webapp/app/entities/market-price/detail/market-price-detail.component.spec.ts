import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MarketPriceDetailComponent } from './market-price-detail.component';

describe('MarketPrice Management Detail Component', () => {
  let comp: MarketPriceDetailComponent;
  let fixture: ComponentFixture<MarketPriceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarketPriceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./market-price-detail.component').then(m => m.MarketPriceDetailComponent),
              resolve: { marketPrice: () => of({ id: 20939 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MarketPriceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketPriceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load marketPrice on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MarketPriceDetailComponent);

      // THEN
      expect(instance.marketPrice()).toEqual(expect.objectContaining({ id: 20939 }));
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
