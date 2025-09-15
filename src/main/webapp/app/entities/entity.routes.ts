import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'pokeStockApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'poke-user',
    data: { pageTitle: 'pokeStockApp.pokeUser.home.title' },
    loadChildren: () => import('./poke-user/poke-user.routes'),
  },
  {
    path: 'user-profile',
    data: { pageTitle: 'pokeStockApp.userProfile.home.title' },
    loadChildren: () => import('./user-profile/user-profile.routes'),
  },
  {
    path: 'card-set',
    data: { pageTitle: 'pokeStockApp.cardSet.home.title' },
    loadChildren: () => import('./card-set/card-set.routes'),
  },
  {
    path: 'card',
    data: { pageTitle: 'pokeStockApp.card.home.title' },
    loadChildren: () => import('./card/card.routes'),
  },
  {
    path: 'inventory-item',
    data: { pageTitle: 'pokeStockApp.inventoryItem.home.title' },
    loadChildren: () => import('./inventory-item/inventory-item.routes'),
  },
  {
    path: 'market-price',
    data: { pageTitle: 'pokeStockApp.marketPrice.home.title' },
    loadChildren: () => import('./market-price/market-price.routes'),
  },
  {
    path: 'trade',
    data: { pageTitle: 'pokeStockApp.trade.home.title' },
    loadChildren: () => import('./trade/trade.routes'),
  },
  {
    path: 'trade-item',
    data: { pageTitle: 'pokeStockApp.tradeItem.home.title' },
    loadChildren: () => import('./trade-item/trade-item.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'pokeStockApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
