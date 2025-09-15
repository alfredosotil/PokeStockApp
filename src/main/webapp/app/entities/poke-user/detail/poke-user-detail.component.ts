import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPokeUser } from '../poke-user.model';

@Component({
  selector: 'jhi-poke-user-detail',
  templateUrl: './poke-user-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PokeUserDetailComponent {
  pokeUser = input<IPokeUser | null>(null);

  previousState(): void {
    window.history.back();
  }
}
