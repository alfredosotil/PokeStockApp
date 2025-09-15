import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITrade } from '../trade.model';
import { TradeService } from '../service/trade.service';

@Component({
  templateUrl: './trade-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TradeDeleteDialogComponent {
  trade?: ITrade;

  protected tradeService = inject(TradeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tradeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
