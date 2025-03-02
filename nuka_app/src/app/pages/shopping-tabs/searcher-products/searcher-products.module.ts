import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SearcherProductsPageRoutingModule } from './searcher-products-routing.module';

import { SearcherProductsPage } from './searcher-products.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SearcherProductsPageRoutingModule
  ],
  declarations: [SearcherProductsPage]
})
export class SearcherProductsPageModule {}
