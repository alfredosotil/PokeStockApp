import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TradeResolve from './route/trade-routing-resolve.service';

const tradeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/trade.component').then(m => m.TradeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/trade-detail.component').then(m => m.TradeDetailComponent),
    resolve: {
      trade: TradeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/trade-update.component').then(m => m.TradeUpdateComponent),
    resolve: {
      trade: TradeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/trade-update.component').then(m => m.TradeUpdateComponent),
    resolve: {
      trade: TradeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tradeRoute;
