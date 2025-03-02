import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ShoppingHistoryPageRoutingModule } from './shopping-history-routing.module';

import { ShoppingHistoryPage } from './shopping-history.page';
import { PipesModule } from 'src/app/pipes/pipes.module';
import { ComponentsModule } from 'src/app/components/components.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ShoppingHistoryPageRoutingModule,
    PipesModule,
    ComponentsModule
  ],
  declarations: [ShoppingHistoryPage]
})
export class ShoppingHistoryPageModule {}
