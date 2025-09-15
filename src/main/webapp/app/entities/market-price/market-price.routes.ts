import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MarketPriceResolve from './route/market-price-routing-resolve.service';

const marketPriceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/market-price.component').then(m => m.MarketPriceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/market-price-detail.component').then(m => m.MarketPriceDetailComponent),
    resolve: {
      marketPrice: MarketPriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/market-price-update.component').then(m => m.MarketPriceUpdateComponent),
    resolve: {
      marketPrice: MarketPriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/market-price-update.component').then(m => m.MarketPriceUpdateComponent),
    resolve: {
      marketPrice: MarketPriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default marketPriceRoute;
