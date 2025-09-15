import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PokeUserResolve from './route/poke-user-routing-resolve.service';

const pokeUserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/poke-user.component').then(m => m.PokeUserComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/poke-user-detail.component').then(m => m.PokeUserDetailComponent),
    resolve: {
      pokeUser: PokeUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/poke-user-update.component').then(m => m.PokeUserUpdateComponent),
    resolve: {
      pokeUser: PokeUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/poke-user-update.component').then(m => m.PokeUserUpdateComponent),
    resolve: {
      pokeUser: PokeUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pokeUserRoute;
