import { Component, OnInit } from '@angular/core';
import { ShoppingList } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { DateTimeFormatOptions } from 'luxon';
import { ConfigService } from 'src/app/config/config.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-shopping-history',
  templateUrl: './shopping-history.page.html',
  styleUrls: ['./shopping-history.page.scss'],
})
export class ShoppingHistoryPage implements OnInit {

  listasDeLaCompra: ShoppingList[] = [];
  loading: boolean = true;

  paginaActual!: number;
  itemPorPagina: number | any;
  totalItems: number | any;
  numeroPaginas: number | any;

  constructor(
    public dataManagementService: DataManagementService,
    public config: ConfigService,
    public utils: UtilsService 
  ) { 
    this.itemPorPagina = this.config.config().itemPorPagina;
  }

  public async cambioPagina(event: any) {
    if (this.paginaActual) {
      this.loading = true;
      if ('back' == event) {
        this.paginaActual-=1; 
      } else if ('next' == event) {
        this.paginaActual+=1;
      } else {
        this.paginaActual=(Number(event));
      }
      this.listasDeLaCompra = await this.dataManagementService.getShoppingHistory(this.paginaActual - 1, this.itemPorPagina);
      this.totalItems = await this.dataManagementService.getNumShoppingHistory();
      this.loading = false;
    }
  
  }

  public async handleRefresh(event: any) {
    this.loading = true;
    this.paginaActual = 1;
    await this.init();
    this.loading = false;
    event?.target?.complete();
  }

  public async init() {
    this.listasDeLaCompra = await this.dataManagementService.getShoppingHistory(this.paginaActual - 1, this.itemPorPagina);
    this.totalItems = await this.dataManagementService.getNumShoppingHistory();
    this.numeroPaginas = Math.ceil(this.totalItems / this.itemPorPagina);
  }

  async ngOnInit() {
    this.loading = true;
    this.paginaActual = 1;
    await this.init();
    this.loading = false;
  }

  public tranformDate(datePurchase: string | any): string {
    const timestamp = parseInt(datePurchase, 10);
    const fecha = new Date(timestamp);
    return String(fecha.getDate()) + ' ' + this.obtenerNombreMes(fecha.getMonth()) + ' ' + fecha.getFullYear().toString() + ' ('+ this.obtenerTiempoTranscurrido(timestamp) +')'
  }

  public obtenerNombreMes(numero: number): string | null {
    if (numero < 1 || numero > 12) {
      return null; // El número está fuera del rango válido de meses
    }
    const fecha = new Date();
    fecha.setMonth(numero);
    const opciones: DateTimeFormatOptions = { month: 'long' };
    const nombreMes = fecha.toLocaleDateString(undefined, opciones);
    return nombreMes;
  }

  public obtenerTiempoTranscurrido(timestamp: number): string {
    return this.utils.obtenerTiempoTranscurrido(timestamp);
  }

}
