import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SearcherProductsPage } from './searcher-products.page';

const routes: Routes = [
  {
    path: '',
    component: SearcherProductsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SearcherProductsPageRoutingModule {}
