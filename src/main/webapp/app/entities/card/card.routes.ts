import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CardResolve from './route/card-routing-resolve.service';

const cardRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/card.component').then(m => m.CardComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/card-detail.component').then(m => m.CardDetailComponent),
    resolve: {
      card: CardResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/card-update.component').then(m => m.CardUpdateComponent),
    resolve: {
      card: CardResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/card-update.component').then(m => m.CardUpdateComponent),
    resolve: {
      card: CardResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cardRoute;
