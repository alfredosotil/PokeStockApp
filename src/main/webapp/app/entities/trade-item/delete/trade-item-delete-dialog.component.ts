import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITradeItem } from '../trade-item.model';
import { TradeItemService } from '../service/trade-item.service';

@Component({
  templateUrl: './trade-item-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TradeItemDeleteDialogComponent {
  tradeItem?: ITradeItem;

  protected tradeItemService = inject(TradeItemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tradeItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
