import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICard } from '../card.model';

@Component({
  selector: 'jhi-card-detail',
  templateUrl: './card-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CardDetailComponent {
  card = input<ICard | null>(null);

  previousState(): void {
    window.history.back();
  }
}
