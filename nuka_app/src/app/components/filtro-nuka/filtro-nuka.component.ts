import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { IonSelect } from '@ionic/angular';

@Component({
  selector: 'app-filtro-nuka',
  templateUrl: './filtro-nuka.component.html',
  styleUrls: ['./filtro-nuka.component.scss'],
})
export class FiltroNukaComponent implements OnInit {

  @Input() tipo: string | any;
  @Input() titulo: string | any;
  @Input() entrada: any[] | any;
  @Input() entradasSeleccion: any[] | any;
  @Input() selectParametro: string | any;

  @Output() salidaEvent: EventEmitter<any[]> = new EventEmitter<any[]>();
  
  @ViewChild('multiSelect', { static: false }) multiSelect: IonSelect | undefined;

  salida: any[] = []
  values: any[] = []

  calendarioAbierto = false;

  constructor() { }

  ngOnInit() {
  }

  toogleCalendario () {
    this.calendarioAbierto = !this.calendarioAbierto;
  }

  filtro_SELECT_MULTIPLE_changed(event: any) {
    return this.salidaEvent.emit(this.multiSelect?.value);
  }

  filtro_SELECT_MULTIPLE_Dismiss() {   
    return this.salidaEvent.emit(this.multiSelect?.value);
  }

  filtro_ORDER_BY_changed(event: any) {
    if (event?.detail?.value == 'Antiguas primero') {
      this.salida = this.entrada.sort((c1:any, c2:any) => {
        if (c1.fechaInicio > c2.fechaInicio) {
          return 1;
        }
        if (c1.fechaInicio < c2.fechaInicio) {
          return -1;
        }
        return 0;
      })   
    } else {
      this.salida = this.entrada.sort((c1:any, c2:any) => {
        if (c1.fechaInicio > c2.fechaInicio) {
          return -1;
        }
        if (c1.fechaInicio < c2.fechaInicio) {
          return 1;
        }
        return 0;
      })    
    }

    return this.salidaEvent.emit(this.salida);
  }
  
  
  filtro_TIME_changed(event: any) {
    return this.salidaEvent.emit(event?.detail?.value);
  }

}
