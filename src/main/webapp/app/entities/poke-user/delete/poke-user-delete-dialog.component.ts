import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPokeUser } from '../poke-user.model';
import { PokeUserService } from '../service/poke-user.service';

@Component({
  templateUrl: './poke-user-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PokeUserDeleteDialogComponent {
  pokeUser?: IPokeUser;

  protected pokeUserService = inject(PokeUserService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pokeUserService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
