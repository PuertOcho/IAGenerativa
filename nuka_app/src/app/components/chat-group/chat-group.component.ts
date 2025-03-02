import { Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import { IonContent, IonItemSliding } from '@ionic/angular';
import { DateTimeFormatOptions } from 'luxon';
import { NukaChat } from 'src/app/models/nuka.model';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-chat-group',
  templateUrl: './chat-group.component.html',
  styleUrls: ['./chat-group.component.scss'],
})
export class ChatGroupComponent implements OnChanges {
  @Input() messages: any[] | undefined;
  @Input() isPageScrolling = false;
  @Input() esperandoRespuestaMensaje: boolean | undefined;
  @Input() borrarMensajes: boolean | undefined;
  
  @Output() scrollToBottomEvent: EventEmitter<any> = new EventEmitter<any>();

  messagesByTime = new Map;
  messageKeys: string[] = [];
  constructor(
    public utils: UtilsService
  ) { }

  messageDraged(event: any, slidingItem: IonItemSliding) {
    if (event.detail.ratio === 1) {
      slidingItem.closeOpened();
    }
  }

  ngOnChanges() {
    if (this.messages) {
      this.messagesByTime = this.messages.reduce((map, message) => {
        const dia = this.tranformDate(message);
        if (map.has(dia)) {
          map.get(dia).messages.push(message);
        } else {
          map.set(dia, {messages: [message] });
        }
        return map;
      }, new Map<string, { dia: string, messages: any[] }>());

      this.messageKeys = [...this.messagesByTime.keys()];
    }
  }

  public tranformDate(message: any): string {
    const timestamp = parseInt(message.fecha, 10);
    const fecha = new Date(timestamp);
    return String(fecha.getDate()) + ' de ' + this.obtenerNombreMes(fecha.getMonth()) + ' de ' + fecha.getFullYear().toString() + ' ('+ this.obtenerTiempoTranscurrido(timestamp) +')'
  }

  public obtenerNombreMes(numero: number): string | null {
    if (numero < 0 || numero > 11) {
      return null; // El número está fuera del rango válido de meses
    }
    const fecha = new Date();
    fecha.setMonth(numero);
    const opciones: DateTimeFormatOptions = { month: 'long' };
    const nombreMes = fecha.toLocaleDateString(undefined, opciones);
    return this.utils.primeraMayuscula(nombreMes);
  }

  public obtenerTiempoTranscurrido(timestamp: number): string {
    return this.utils.obtenerTiempoTranscurrido(timestamp);
  }

  scrollToBottom() {
    this.scrollToBottomEvent.emit();
  }
}
