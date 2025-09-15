import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TradeItemResolve from './route/trade-item-routing-resolve.service';

const tradeItemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/trade-item.component').then(m => m.TradeItemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/trade-item-detail.component').then(m => m.TradeItemDetailComponent),
    resolve: {
      tradeItem: TradeItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/trade-item-update.component').then(m => m.TradeItemUpdateComponent),
    resolve: {
      tradeItem: TradeItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/trade-item-update.component').then(m => m.TradeItemUpdateComponent),
    resolve: {
      tradeItem: TradeItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tradeItemRoute;
