import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICard } from '../card.model';
import { CardService } from '../service/card.service';

@Component({
  templateUrl: './card-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CardDeleteDialogComponent {
  card?: ICard;

  protected cardService = inject(CardService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
