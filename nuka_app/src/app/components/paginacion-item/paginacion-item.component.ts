import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-paginacion-item',
  templateUrl: './paginacion-item.component.html',
  styleUrls: ['./paginacion-item.component.scss'],
})
export class PaginacionItemComponent implements OnInit, OnChanges {

  @Input() paginaActual!: number;
  @Input() itemPorPagina!: number;
  @Input() totalItems!: number;
  @Output() cambioPaginaEvent: EventEmitter<any> = new EventEmitter<any>();
  
  numeroPaginas: number | any;
  arrayNumeroPaginas: number[] | undefined;
  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.itemPorPagina && this.totalItems) {
      this.numeroPaginas = Math.ceil(this.totalItems / this.itemPorPagina);
      this.arrayNumeroPaginas = this.numerosCercanos(Array.from({ length: this.numeroPaginas }, (_, i) => i + 1), this.paginaActual) ;
    }
  }

  public numerosCercanos(array: number[], indice: number): number[] {
    if (indice < 0 || indice > array.length) {
      throw new Error("El índice está fuera de los límites del array.");
    }
    const tamArray: number = array.length;
    if (tamArray >= 5) {
      if (indice == 1 || indice ==  2) {
        return [1,2,3,4,5]
      } else if (indice == tamArray || indice == tamArray - 1) {
        return [tamArray-4, tamArray-3, tamArray-2, tamArray-1 ,tamArray]
      } else {
        return [indice-2, indice-1, indice, indice+1, indice+2]
      }
    }
    return array
  }

  desahabilitarBack() {
    return this.paginaActual <= 1;
  }
  desahabilitarNext() {
    return this.paginaActual >= this.numeroPaginas;
  }

  assinarColoBadge(item: number) {
    let CssStyles = null;
    if (item == this.paginaActual) {
      CssStyles = {
        '--background': 'var(--ion-color-primary)',
        'color': 'var(--ion-color-primary-contrast)',
        'border-radius': '50%',
        'padding-right': '5px',
        'padding-left': '5px',

      };
    } else {
      CssStyles = {        
        '--background': 'none'
      };
    }
    return CssStyles;
  }

  ngOnInit() {}

  siguientePagina() {
    this.cambioPaginaEvent.emit('next')
  }

  retrocederPagina() {
    this.cambioPaginaEvent.emit('back')
  }

  moverAPagina(n: number) {
    this.cambioPaginaEvent.emit(n)
  }

}
