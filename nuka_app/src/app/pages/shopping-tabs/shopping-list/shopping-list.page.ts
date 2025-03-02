import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ItemReorderEventDetail } from '@ionic/angular';
import { ProductRelation, ShoppingList } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.page.html',
  styleUrls: ['./shopping-list.page.scss'],
})
export class ShoppingListPage implements OnInit {
  products: any = []

  productsNoMarcados: ProductRelation[] = []
  productsMarcados: ProductRelation[] = []
  isLoading: boolean = false;

  shoppingList: ShoppingList | any;
  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public router: Router
  ) { }

  assinarColorDelBorde(item: ProductRelation | null) {
    let CssStyles = null;
    if (item && item.marcado) {
      CssStyles = {        
        '--border-color': this.utils.getColorCategoriaListaCompra('Marcado')
      };
    } else {
      CssStyles = {        
        '--border-color': this.utils.getColorCategoriaListaCompra(item?.productInfo?.categoria)
      };
    }

    return CssStyles;
  }



  async confirmarProductosSeleccionados() {
    this.isLoading = true;
    this.shoppingList = await this.dataManagementService.confirmarProductosSeleccionados(this.shoppingList.id, this.productsMarcados);
    this.productsMarcados = [];
    this.router.navigate(['shopping-tabs/shopping-history']);
    this.isLoading = false;
  }

  async ngOnInit() {
    this.isLoading = true;
    this.shoppingList = await this.dataManagementService.getShoppingCart();
    this.productsNoMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => !pr.marcado);
    this.productsMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => pr.marcado);
    this.isLoading = false;
  }
  
  onInputNumber(ev: any, item: any) {
    let pr = this.shoppingList.productRelations.find((psl: any) => psl.productInfo.id == item.productInfo.id);
    pr.quantity = Number(ev.target!.value);

    this.actualizarProductoMarcadosYNM();

  }

  checkboxClick(e: any, item: any){
    item.marcado = e.detail.checked;
    this.productsNoMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => !pr.marcado);
    this.productsMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => pr.marcado);
  }

  handleReorder(ev: CustomEvent<ItemReorderEventDetail>) {
    // The `from` and `to` properties contain the index of the item
    // when the drag started and ended, respectively
    //console.log('Dragged from index', ev.detail.from, 'to', ev.detail.to);

    // Finish the reorder and position the item in the DOM based on
    // where the gesture ended. This method can also be called directly
    // by the reorder group
    this.shoppingList.productRelations = ev.detail.complete(this.shoppingList.productRelations);
    this.productsNoMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => !pr.marcado);
    this.productsMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => pr.marcado);

  }

  actualizarProductoMarcadosYNM() {
    this.productsNoMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => !pr.marcado);
    this.productsMarcados = this.shoppingList.productRelations.filter((pr: ProductRelation) => pr.marcado);
  }
}
