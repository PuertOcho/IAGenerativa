import { Component, TemplateRef, Input, OnInit, EventEmitter, Output } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { ContenidoMsj, NukaMsj, OpcionMsj, OpcionesMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';
import { OptionModalComponent } from '../option-modal/option-modal.component';
import { EventsService } from 'src/app/services/events.service';

@Component({
  selector: 'app-options-item',
  templateUrl: './options-item.component.html',
  styleUrls: ['./options-item.component.scss'],
})
export class OptionsItemComponent implements OnInit {
  @Input() message: NukaMsj | undefined;
  @Input() contenido: ContenidoMsj | any;
  @Output() nuevoMsj: EventEmitter<any> = new EventEmitter<any>();

  opciones: OpcionesMsj | undefined;
  isLoading: boolean = true;

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public modalCtrl: ModalController,
    public eventsService: EventsService) { }

  ngOnInit() {
    this.isLoading = true;
    this.getResourceOptions(this.contenido.id).finally(() => {
      this.isLoading = false;
    });
  }

  async getResourceOptions(id: string) {
      const blob = await this.dataManagementService.getResource(id, 'jsonOpciones');
      if (blob != null) {
        const text = await blob.text(); // Convierte el Blob a una cadena de texto
        try {
          this.opciones = JSON.parse(text);
        } catch (error) {
          console.error("Error al analizar el JSON:", error);
        }
      }
      return;
  }

  public async accionDeOpcion(opcion: OpcionMsj) {
    if (opcion?.accion?.endpoint && opcion?.accion?.datos) {
      let datos: any = opcion?.accion?.datos;
      if (this.message?.usuarioId && this.message?.chatId) {
        datos['usuarioId'] = this.message?.usuarioId;
        datos['chatId'] = this.message?.chatId;
      }
      await this.eventsService.publish('esperandoRespuestaMensaje', true);
      const nukaMsj: NukaMsj = await this.dataManagementService.customGet(opcion?.accion?.endpoint, datos);
      await this.eventsService.publish('esperandoRespuestaMensaje', false);
      this.dispararEventoNuevoMsj(nukaMsj);
    } else if (opcion?.accion?.endpoint && opcion?.accion?.necesitaDatos && opcion?.accion?.datosNecesarios) {
      this.abrirModalOpciones(opcion);
    } else {
      this.utils.mostrarAlerta();
    }
  }

  dispararEventoNuevoMsj(nukaMsj: NukaMsj) {
    this.nuevoMsj.emit(nukaMsj);
  }

  async abrirModalOpciones(opcion: OpcionMsj) {
    const modal = await this.modalCtrl.create({
      component: OptionModalComponent,
      componentProps: {
        'opcion': opcion,
        'message': this.message
      }      
    });
    await modal.present();

    modal.onWillDismiss().then(async (data) => {
      if (data?.data?.listo) {
        await this.eventsService.publish('esperandoRespuestaMensaje', true);
        this.dispararEventoNuevoMsj(data.data.nukaMsj as NukaMsj);
      }
    });
  }

}
