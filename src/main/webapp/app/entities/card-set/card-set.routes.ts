import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CardSetResolve from './route/card-set-routing-resolve.service';

const cardSetRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/card-set.component').then(m => m.CardSetComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/card-set-detail.component').then(m => m.CardSetDetailComponent),
    resolve: {
      cardSet: CardSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/card-set-update.component').then(m => m.CardSetUpdateComponent),
    resolve: {
      cardSet: CardSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/card-set-update.component').then(m => m.CardSetUpdateComponent),
    resolve: {
      cardSet: CardSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cardSetRoute;
