import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IInventoryItem } from '../inventory-item.model';

@Component({
  selector: 'jhi-inventory-item-detail',
  templateUrl: './inventory-item-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class InventoryItemDetailComponent {
  inventoryItem = input<IInventoryItem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
