import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ITradeItem } from '../trade-item.model';

@Component({
  selector: 'jhi-trade-item-detail',
  templateUrl: './trade-item-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TradeItemDetailComponent {
  tradeItem = input<ITradeItem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
