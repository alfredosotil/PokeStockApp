import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITrade } from '../trade.model';

@Component({
  selector: 'jhi-trade-detail',
  templateUrl: './trade-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TradeDetailComponent {
  trade = input<ITrade | null>(null);

  previousState(): void {
    window.history.back();
  }
}
