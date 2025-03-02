import { Component, Input, OnInit, EventEmitter, Output, ViewChild } from '@angular/core';
import { AlertController, ItemReorderEventDetail, ModalController } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { ConfigService } from 'src/app/config/config.service';
import { OpcionBasica, OpcionesModal, User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-options-modal',
  templateUrl: './options-modal.component.html',
  styleUrls: ['./options-modal.component.scss'],
})
export class OptionsModalComponent implements OnInit {
  
  @Input() esAjustesChat: boolean| any;
  @Input() id: string | any;
  @Input() opcionesModal: OpcionesModal | any;
  @Output() salida: EventEmitter<any> = new EventEmitter<any>();

  datosNecesarios: any;
  datosNecesariosKeys: string[] = [];
  isLoading: boolean = true;
  datosGuardados: {[datoKey: string]: any} = {};
  etiquetasKeys: string[] = [];
  isModalImagenesOpen: boolean = false;
  usuario: User | any;
  versionServidor: string | any; 
  mostrarMensajeDatosModificados: boolean = false;

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public modalCtrl: ModalController,
    public eventsService: EventsService,
    public appData: AppDataModel,
    public alertController: AlertController,
    public config: ConfigService) {}

  handleReorder(ev: CustomEvent<ItemReorderEventDetail>, opcionBasica: OpcionBasica) {
      this.guardarDatos(opcionBasica, ev.detail.complete(opcionBasica.valor), 'REORDER');
  }
    
  cambiarImagen() {
      this.isModalImagenesOpen = true;
  }

  selectImagen(opcionBasica: OpcionBasica, img: any) {
      opcionBasica.valor = []
      opcionBasica.valor?.push(img.id);
      this.guardarDatos(opcionBasica, img)
      this.isModalImagenesOpen = false
  }

  getPathImage(name: string) {
      return this.appData.PATH_IMG_AJUSTES + name;
  }

  getImagenPath(opcionBasica: OpcionBasica): string {
      if (opcionBasica && opcionBasica.valor && opcionBasica.valor?.length > 0) {
          const bool = this.appData.IMG_INFO.some(i => i.name == (opcionBasica as any).valor[0]);
          if (bool) {
            return this.appData.PATH_IMG_AJUSTES + (opcionBasica as any).valor[0];
          }
      }
      return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name;
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
    
  public mostarMensajeDeCambios() {
      if (this.datosGuardados && Object.keys(this.datosGuardados).length > 0) {
        this.mostrarMensajeDatosModificados = true;
        setTimeout(()=> {
          this.mostrarMensajeDatosModificados = false;
        }, 10000);
      }
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

  public async guardarAjustes(opcionBasica: OpcionBasica) {
    this.isLoading = true;
    if (opcionBasica.valor && this.datosGuardados && Object.keys(this.datosGuardados).length > 0) {
      let params: any = {};
      const jsonString = JSON.stringify(this.datosGuardados)
      params['id'] = this.id;
      params['params'] = jsonString;
      const response: any = await this.dataManagementService.customGet(opcionBasica.valor[0], params);
      if (response) {
        this.datosGuardados = {};
        if (this.esAjustesChat) {
          this.isLoading = false;
          this.mostrarMensajeDatosModificados = false;
          this.eventsService.publish('actualizarOpcionesAjustes');
          this.modalCtrl.dismiss( { salida: response } );
        } else {
          this.isLoading = false;
          this.mostrarMensajeDatosModificados = false;
          this.salida.emit(response);
        }
      }
    } else {
      this.isLoading = false;
      this.utils.mostrarAlerta('No se guardó', 'Parece que no se han guardado los datos en opcionBasica o datosGuardados correctamente');
    }
  }

  public async cerrarSesion() {
    const botones = [
      { text: "Si", role: "si" },
      { text: "No", role: "no" }];
    const alertaCierreSesion =  await this.utils.mostrarAlerta('Cierre de sesión', '¿Seguro que quieres cerrar la sesión?', botones);
    alertaCierreSesion.onWillDismiss().then( async (data) => {
      if (data && data.role &&  data.role == 'si') {
        await this.dataManagementService.cerrarSesion();
      }
    });
  }

  public async accionDeBoton(opcionBasica: OpcionBasica) {
      if (opcionBasica && opcionBasica.id && opcionBasica.id.indexOf('GENERAL_GUARDAR_AJUSTES') > -1) {
        await this.guardarAjustes(opcionBasica);
      } else if (opcionBasica && opcionBasica.id && opcionBasica.id == 'GENERAL_CERRAR_SESION') {
        await this.cerrarSesion();
      } else if (opcionBasica && opcionBasica.valor) {
        let params: any = {};
        params['id'] = this.id;
        const response: any = await this.dataManagementService.customGet(opcionBasica.valor[0], params);
        console.log('response', response)
      }
  }

  async ngOnInit() {
    this.usuario = await this.dataManagementService.getUsuario();
    this.isLoading = true;
    this.etiquetasKeys = this.opcionesModal.opciones?.map((o : any) => o.etiqueta) as string[];
    this.isLoading = false;
  }

  public getValuesOpcionIfNotSaved(opcionBasica: OpcionBasica): string[] {
    if (opcionBasica && opcionBasica.id && this.datosGuardados[opcionBasica.id]) {
      return this.datosGuardados[opcionBasica.id];
    } else  {
      return this.utils.getValuesOpcion(opcionBasica)
    }
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
      } else if (opcionBasica.tipo == 'SELECT_MULTIPLE' && event?.detail?.value && event?.detail?.value.length > 0) {
        this.datosGuardados[opcionBasica.id] = event?.detail?.value;
      } else if (opcionBasica.tipo == 'RANGE') {
        this.datosGuardados[opcionBasica.id] = [String(event?.detail?.value)];
      } else if (opcionBasica.tipo == 'SELECT_SINGLE') {
        this.datosGuardados[opcionBasica.id] = [event?.detail?.value];
      } else if (opcionBasica.tipo == 'INPUT') {
        this.datosGuardados[opcionBasica.id] = [event?.detail?.value];
      }
    }
    
    this.mostarMensajeDeCambios();
  }

  public comprobarDatos(): boolean {
    const datosGuardadosKeys = Object.keys(this.datosGuardados as any);
    return datosGuardadosKeys.every(dg => this.datosNecesariosKeys.indexOf(dg) > 0);
  } 

  getTipoDato(key: any) {
    if (!this.datosNecesarios) {
      //this.datosNecesarios = this.opcion?.accion?.datosNecesarios;
    }
    return this.datosNecesarios[key]['tipo'];
  }

  cancel() {
    this.modalCtrl.dismiss({ listo: false }); 
  }

}
