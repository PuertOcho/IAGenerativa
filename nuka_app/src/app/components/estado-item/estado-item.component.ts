import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-estado-item',
  templateUrl: './estado-item.component.html',
  styleUrls: ['./estado-item.component.scss'],
})
export class EstadoItemComponent implements OnInit {
  @Input() item: any;
  @Input() classItem: string | undefined;
  @Output() notificacionesEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() tareasEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() calendarioEvent: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    public utils: UtilsService
  ) {
  }

  ngOnInit() {
  }

  public onLongPress(event: any) {
    console.log('Presión larga detectada en chat-item', event);
  }
  
  cambiarEstadoMsj() {
  }

  cerrarEstadoMsj() {

  }

  // NOTIFICACIONES
  assinarEstiloNotificacion(item: any) {
    let CssStyles = {};
    if (item && item.fecha && item.fecha > 0) {
      const fechaActual = new Date();
      const timestampActual = fechaActual.getTime();
      if (timestampActual > item.fecha) {
        CssStyles = {   
          'opacity': '0.5'
        };
      }
    }
    return CssStyles;
  }

  // TAREAS
  assinarEstiloCirculo(item: any) {
    let CssStyles = null;
    let colorCirculo = 'grey'
    if (item && item.fecha && item.tiempoRepeticion) {
      const diasRestantes = this.getDiasRestantes(item);
      if (diasRestantes <= 0) {
        colorCirculo = 'var(--ion-color-danger)';
      } else if (diasRestantes == 1 || diasRestantes == 2) {
        colorCirculo = 'var(--ion-color-warning)';
      } else if (diasRestantes >= 3) {
        colorCirculo = 'var(--ion-color-primary)';
      }
    }
    CssStyles = {   
      'background-color': colorCirculo
    };
    return CssStyles;
  }
  getDiasRestantes(item: any) {
    const ahoraTimestamp = Date.now();
    const diferenciaEnMilisegundos = ahoraTimestamp - item.fecha;
    const milisegundosEnUnDia = 24 * 60 * 60 * 1000;
    const diasPasados = Math.floor(diferenciaEnMilisegundos / milisegundosEnUnDia);
    const diasRestantes: number = item.tiempoRepeticion - diasPasados;
    if (diasRestantes <= 0) {
      return 0;
    } else {
      return diasRestantes;
    }
  }

  terminarTareaPorHacer(event: any) {
      const element = document.getElementById(this.item.id);    
      if (element != null) {
        element.classList.add('animated-erase-out');
        setTimeout(() => {
          return event && event.detail && event.detail.checked ?
          this.tareasEvent.emit(
            { accion: 'terminarTareaPorHacer', 
              item: this.item,
              diasRestantes: this.getDiasRestantes(this.item),
              checked: event.detail.checked
            }):
          null;
        }, 400);
      }
      return null;
  }

  agregarTareaPorHacer() {
    return this.tareasEvent.emit({accion: 'agregarTareaPorHacer', item: this.item});
  }

  // NOTIFICACIONES
  public redireccionPorNotificacion() {
    return this.notificacionesEvent.emit({accion: 'redireccionPorNotificacion', item: this.item});
  }

  public cerrarNotificacion() {
    const element = document.getElementById(this.item.id);    
    if (element != null) {
      element.classList.add('animated-erase-out');
      setTimeout(() => {
        element.classList.add('display-none');
        return this.notificacionesEvent.emit({accion: 'cerrarNotificacion', item: this.item});
      }, 400);
    }
    return null;
  }
}
