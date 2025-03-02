import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ShoppingTabsPage } from './shopping-tabs.page';

const routes: Routes = [
  {
    path: '',
    component: ShoppingTabsPage,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'shopping-list',
      },
      {
        path: 'shopping-list',
        loadChildren: () => import('../shopping-list/shopping-list.module').then( m => m.ShoppingListPageModule)
      },
      {
        path: 'shopping-history',
        loadChildren: () => import('../shopping-history/shopping-history.module').then( m => m.ShoppingHistoryPageModule)
      },
      {
        path: 'searcher-products',
        loadChildren: () => import('../searcher-products/searcher-products.module').then( m => m.SearcherProductsPageModule)
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ShoppingTabsPageRoutingModule {}
