import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICardSet } from '../card-set.model';

@Component({
  selector: 'jhi-card-set-detail',
  templateUrl: './card-set-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class CardSetDetailComponent {
  cardSet = input<ICardSet | null>(null);

  previousState(): void {
    window.history.back();
  }
}
