import { Component, Input, OnInit } from '@angular/core';
import { ItemReorderEventDetail } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { NukaChat, OpcionBasica, OpcionesModal } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-opciones-rapidas-item',
  templateUrl: './opciones-rapidas-item.component.html',
  styleUrls: ['./opciones-rapidas-item.component.scss'],
})
export class OpcionesRapidasItemComponent implements OnInit {

  constructor(
    public utils: UtilsService,
    public dataManagementService: DataManagementService,
    public eventsService: EventsService,
    public appData: AppDataModel
  ) { }

  @Input() mostrarBarraBusqueda!: boolean;
  @Input() opcionesRapidasCompleta!: boolean;
  @Input() chat: NukaChat | any;

  isModalImagenesOpen: boolean = false;
  isLoading: boolean = false;
  opcionesRapidas: OpcionBasica[] | any = [];
  datosGuardados: {[datoKey: string]: any} = {};

  ngOnInit() {
    console.log('opcionesModal', this.chat.opcionesModal);

    let opcionesBasicasAjustesRapidos: OpcionBasica = this.utils.getOpcionBasica(this.chat.opcionesModal, 'AJUSTES RAPIDOS', 'AJUSTES_RAPIDOS_OPCIONES');
    let opcionesIds = this.utils.getValuesOpcion(opcionesBasicasAjustesRapidos);
    this.opcionesRapidas = [];
    opcionesIds.forEach((id: string) => {
      let opcionesBasicasAux: OpcionBasica | any = this.utils.getOpcionBasicaById(this.chat.opcionesModal, id);
      if (opcionesBasicasAux) {
        this.opcionesRapidas.push(opcionesBasicasAux);
      }
    }) 
  }

  public async guardarOpcionesRapidas() {
    this.isLoading = true;
    if (this.datosGuardados && Object.keys(this.datosGuardados).length > 0) {
      let params: any = {};
      const jsonString = JSON.stringify(this.datosGuardados)
      params['id'] = this.chat.id;
      params['params'] = jsonString;
      const response: any = await this.dataManagementService.customGet('guardarAjustesChat', params);
      console.log('response', response)
      if (response) {
        this.datosGuardados = {};
          this.isLoading = false;
          this.eventsService.publish('actualizarOpcionesAjustes');
      } else {
        this.isLoading = false;
        this.utils.mostrarAlerta('No se guardó', 'Parece que no se han guardado los datos en opcionBasica o datosGuardados correctamente');
      }
    } else {
      this.isLoading = false;
      this.utils.mostrarAlerta('No se guardó', 'Parece que no se han guardado los datos en opcionBasica o datosGuardados correctamente');
    }

  }

  public assinarEstiloIconoSave() {
    let CssStyles = null;
    if (this.datosGuardados &&  Object.keys(this.datosGuardados).length > 0) {
      CssStyles = {        
        'color': 'var(--ion-color-danger)',
      };
    } else {
      CssStyles = {        
        'color': 'var(--ion-color-primary)',
      };
    }
    return CssStyles;
  }

  public assinarEstilo(item: any | null) {
    let CssStyles = null;
    const datosGuardadosKeys = Object.keys(this.datosGuardados);
    if (item && this.datosGuardados &&  datosGuardadosKeys.some(d => d === item.id) ) {
      CssStyles = {        
        'color': 'var(--ion-color-danger)',
      };
    }
    return CssStyles;
}

public mostrarBotonesOpcionesRapidas() {
  return this.opcionesRapidasCompleta && this.opcionesRapidas && this.opcionesRapidas.length > 0;
}

assinarEstiloOpcionesRapidas() {
    let CssStyles = null;
    if(this.opcionesRapidasCompleta) {
      if (this.opcionesRapidas && this.opcionesRapidas.length > 0) {
        CssStyles = {
          'height': '300px',
          'width': '250px',
          'border-radius': '0px 15px 15px 15px',
          'overflow': 'scroll',
          'background-color': 'var(--ion-color-light-shade)',
          'color': 'var(--ion-color-light-shade-contrast)'
        };
      } else {
        CssStyles = {
          'height': '90px',
          'width': '250px',
          'border-radius': '15px 15px 15px 15px',
          'background-color': 'var(--ion-color-light-shade)',
          'color': 'var(--ion-color-light-shade-contrast)'
        };
      }

    } else {
      CssStyles = {
        'height': '60px',
        'overflow': 'hidden',
        'width': '60px'
      };
    }
    return CssStyles;
  }

  cambiarVisibilidadOpcionesRapidas() {
    this.opcionesRapidasCompleta = !this.opcionesRapidasCompleta;
  }

  handleReorder(ev: CustomEvent<ItemReorderEventDetail>, opcionBasica: OpcionBasica) {
    this.guardarDatos(opcionBasica, ev.detail.complete(opcionBasica.valor), 'REORDER');
  }

  async guardarDatos(opcionBasica: OpcionBasica, event: any, casoEspecial?: any) {
    if (opcionBasica.id && opcionBasica.tipo && event) {
      if (opcionBasica.tipo == 'CHECKBOX') {
        this.datosGuardados[opcionBasica.id] = [String(event?.detail?.checked)];
        opcionBasica.valor = [String(event?.detail?.checked)];
      } else if (opcionBasica.tipo == 'SELECT_IMG') {
        this.datosGuardados[opcionBasica.id] = [event.name];
      } else if (opcionBasica.tipo == 'SELECT_MULTIPLE_AND_REORDER') {
        if (casoEspecial && casoEspecial == 'REORDER') {
          opcionBasica.valor = this.datosGuardados[opcionBasica.id] = event;
        } else if (event?.detail?.value && event?.detail?.value.length > 0) {
          opcionBasica.valor = this.datosGuardados[opcionBasica.id] = event?.detail?.value;
        }
      } if (opcionBasica.tipo == 'RANGE') {
        this.datosGuardados[opcionBasica.id] = [String(event?.detail?.value)];
      } else if (opcionBasica.tipo == 'SELECT_SINGLE') {
        this.datosGuardados[opcionBasica.id] = [event?.detail?.value];
      } else if (opcionBasica.tipo == 'INPUT') {
        this.datosGuardados[opcionBasica.id] = [event?.detail?.value];
      }      
    }
  }

  selectImagen(opcionBasica: OpcionBasica, img: any) {
    opcionBasica.valor = []
    opcionBasica.valor?.push(img.id);
    this.guardarDatos(opcionBasica, img);
  }

  public async accionDeBoton(opcionBasica: OpcionBasica) {
    this.isLoading = true;
    if (opcionBasica.valor && this.datosGuardados && Object.keys(this.datosGuardados).length > 0) {
      let params: any = {};
      const jsonString = JSON.stringify(this.datosGuardados)
      params['id'] = this.chat.id;
      params['params'] = jsonString;
      const response: any = await this.dataManagementService.customGet(opcionBasica.valor[0], params);
      console.log('response', response)
      if (response) {
        this.datosGuardados = {};
          this.isLoading = false;
          this.eventsService.publish('actualizarOpcionesAjustes');
      }
    } else {
      this.isLoading = false;
      this.utils.mostrarAlerta('No se guardó', 'Parece que no se han guardado los datos en opcionBasica o datosGuardados correctamente');
    }
  }

cancelModalImagenes() {
  this.isModalImagenesOpen = false;
}

getLabelImagen(opcionBasica: OpcionBasica): string {
  if (opcionBasica && opcionBasica.valor){
    let imageInfo = this.appData.IMG_INFO.find(i => i.name == (opcionBasica as any).valor[0])
    if (imageInfo) {
      return imageInfo?.label;
    }
  }
  return this.appData.IMG_INFO[0].label;
}

cambiarImagen() {
  this.isModalImagenesOpen = true;
}

}
