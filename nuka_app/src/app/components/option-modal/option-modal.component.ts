import { Component, TemplateRef, Input, OnInit, EventEmitter, Output, ViewChild } from '@angular/core';
import { IonModal, ModalController } from '@ionic/angular';
import { ContenidoMsj, NukaMsj, OpcionMsj, OpcionesMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-option-modal',
  templateUrl: './option-modal.component.html',
  styleUrls: ['./option-modal.component.scss'],
})
export class OptionModalComponent implements OnInit {
  
  @Input() opcion!: OpcionMsj;
  @Input() message!: NukaMsj;
  @Output() nuevoMsj: EventEmitter<any> = new EventEmitter<any>();

  opciones: OpcionesMsj | undefined;
  datosNecesarios: any;
  datosNecesariosKeys: string[] = [];
  isLoading: boolean = true;
  datosGuardados: {[datoKey: string]: any} = {};

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public modalCtrl: ModalController,
    public eventsService: EventsService) { }

    
  ngOnInit() {
    this.isLoading = true;
    this.datosNecesarios = this.opcion?.accion?.datosNecesarios;
    this.datosNecesariosKeys = Object.keys(this.opcion?.accion?.datosNecesarios as any);
    this.datosNecesariosKeys.forEach( key => {
      const value = this.datosNecesarios[key];
      if (value?.auxiliar) {
        if (value?.tipo == 'texto') {
          this.datosGuardados[key] = value?.auxiliar[0];
        } else if (value?.tipo == 'seleccion') {
          //console.log(value?.auxiliar);
        }
      }
    });
    this.isLoading = false;
  }

  guardarDatos(datoKey: string, event: any) {
    this.datosGuardados[datoKey] = event?.detail?.value;
  }

  public comprobarDatos(): boolean {
    const datosGuardadosKeys = Object.keys(this.datosGuardados as any);
    return datosGuardadosKeys.every(dg => this.datosNecesariosKeys.indexOf(dg) > 0);
  } 

  getDatoTitulo(key: string) {
    if (!this.datosNecesarios) {
      this.datosNecesarios = this.opcion?.accion?.datosNecesarios;
    }
    return this.datosNecesarios[key]['etiqueta'];
  }

  getTipoDato(key: string) {
    if (!this.datosNecesarios) {
      this.datosNecesarios = this.opcion?.accion?.datosNecesarios;
    }
    return this.datosNecesarios[key]['tipo'];
  }

  getAuxiliarDato(key: string) {
    //imagen, texto, seleccion, checkbox, toogle, rango
    if (!this.datosNecesarios) {
      this.datosNecesarios = this.opcion?.accion?.datosNecesarios;
    }
    return this.datosNecesarios[key]['auxiliar'];
  }

  dispararEventoNuevoMsj(nukaMsj: NukaMsj) {
    this.nuevoMsj.emit(nukaMsj);
  }

  cancel() {
    this.modalCtrl.dismiss({ listo: false });
  }

  async confirm() {
    if (this.opcion?.accion?.endpoint) {
      if (this.message?.usuarioId && this.message?.chatId) {
        this.datosGuardados['usuarioId'] = this.message?.usuarioId;
        this.datosGuardados['chatId'] = this.message?.chatId;
      }

      await this.eventsService.publish('esperandoRespuestaMensaje', true);
      const nukaMsj: NukaMsj = await this.dataManagementService.customGet(this.opcion?.accion?.endpoint, this.datosGuardados);
      await this.eventsService.publish('esperandoRespuestaMensaje', false);

      this.modalCtrl.dismiss({ listo: true, nukaMsj: nukaMsj });

    } else {
      this.utils.mostrarAlerta();
    }
  }

}
