import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ShoppingTabsPageRoutingModule } from './shopping-tabs-routing.module';

import { ShoppingTabsPage } from './shopping-tabs.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ShoppingTabsPageRoutingModule
  ],
  declarations: [ShoppingTabsPage]
})
export class ShoppingTabsPageModule {}
