import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMarketPrice } from '../market-price.model';

@Component({
  selector: 'jhi-market-price-detail',
  templateUrl: './market-price-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MarketPriceDetailComponent {
  marketPrice = input<IMarketPrice | null>(null);

  previousState(): void {
    window.history.back();
  }
}
