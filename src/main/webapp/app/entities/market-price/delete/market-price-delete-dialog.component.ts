import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMarketPrice } from '../market-price.model';
import { MarketPriceService } from '../service/market-price.service';

@Component({
  templateUrl: './market-price-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MarketPriceDeleteDialogComponent {
  marketPrice?: IMarketPrice;

  protected marketPriceService = inject(MarketPriceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.marketPriceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
