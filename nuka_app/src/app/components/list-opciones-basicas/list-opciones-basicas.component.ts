import { Component, Input, OnInit } from '@angular/core';
import { ItemReorderEventDetail } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { OpcionBasica, User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-list-opciones-basicas',
  templateUrl: './list-opciones-basicas.component.html',
  styleUrls: ['./list-opciones-basicas.component.scss'],
})
export class ListOpcionesBasicasComponent implements OnInit {
  @Input() id: string | any;
  @Input() listaOpciones: OpcionBasica[] | any;

  public isLoading = false;
  public usuario: User | any;
  datosGuardados: {[datoKey: string]: any} = {};

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public appData: AppDataModel
  ) {}

  public async accionDeBoton(opcionBasica: OpcionBasica) {
    if (opcionBasica && opcionBasica.valor) {
      let params: any = {};
      params['id'] = this.id;
      const response: any = await this.dataManagementService.customGet(opcionBasica.valor[0], params);
      console.log('response', response)
    }
}

  public getValuesOpcionIfNotSaved(opcionBasica: OpcionBasica): string[] {
    if (opcionBasica && opcionBasica.id && this.datosGuardados[opcionBasica.id]) {
      return this.datosGuardados[opcionBasica.id];
    } else  {
      return this.utils.getValuesOpcion(opcionBasica)
    }
  }

  async ngOnInit() {
    this.usuario = await this.dataManagementService.getUsuario();
    this.isLoading = true;

    
    this.isLoading = false;
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
  }


  public mostrarEtiquetaDeOpcion(opcionBasica: OpcionBasica): boolean{
    return opcionBasica.tipo != 'NUMBER' && opcionBasica.tipo != 'INPUT' && opcionBasica.tipo != 'BOTON';
  }

}
