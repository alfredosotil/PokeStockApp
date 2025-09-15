import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICardSet } from '../card-set.model';
import { CardSetService } from '../service/card-set.service';

@Component({
  templateUrl: './card-set-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CardSetDeleteDialogComponent {
  cardSet?: ICardSet;

  protected cardSetService = inject(CardSetService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardSetService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
