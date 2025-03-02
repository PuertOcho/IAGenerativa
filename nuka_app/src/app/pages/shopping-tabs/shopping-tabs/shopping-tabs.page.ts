import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-shopping-tabs',
  templateUrl: './shopping-tabs.page.html',
  styleUrls: ['./shopping-tabs.page.scss'],
})
export class ShoppingTabsPage implements OnInit {

  tabs: any[] = [
    { id: 'shopping-list', icon: 'cart-outline', label: 'Lista de la compra' },
    { id: 'shopping-history', icon: 'newspaper-outline', label: 'Historial de compras' },
    { id: 'shopping-history', label: 'Historial de compras', image: 'assets/icon/shopping-history.png' }
  ];
  
  constructor() { }

  ngOnInit() {
  }

}
