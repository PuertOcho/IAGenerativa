import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { NavController } from '@ionic/angular';
import { ConfigService } from 'src/app/config/config.service';
import { User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { NotificacionesService } from 'src/app/services/notificaciones-service.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss'],
})
export class SettingsPage implements OnInit, OnChanges {

  loading: boolean = false;
  @Input() usuario: User | any;
  ajustesCambiados: any = [];
  etiquetasDesarrolladorKeys: string[] = ['Desarrollador'];
  branchServers: string[] = ['dev', 'master', 'custom'];
  versionServidor: string | any;
  ramaServidor: string | any;
  urlServidor: string | any;
  serviciosToogle: any = [
    { nombreContenedor: 'nuka-app-c', titulo: 'Interfaz grafica', habilitado: null},
    { nombreContenedor: 'jellyseerr-c', titulo: 'Jellyseerr', habilitado: null},
    { nombreContenedor: 'jellyfin-c', titulo: 'Jellyfin', habilitado: null}
  ];
  loadingService: boolean = false;
  disableIonChange: boolean = false;
  constructor(
    public utils: UtilsService,
    public dataManagementService: DataManagementService,
    public navController: NavController,
    public config: ConfigService,
    public eventsService: EventsService,
    public notificacionesService: NotificacionesService
  ) {
  }
  ngOnChanges(changes: SimpleChanges): void {
    console.log('changes', changes);
    console.log('usuario', this.usuario);
  }

  async ngOnInit() {
    this.usuario = await this.dataManagementService.getUsuario();
    this.disableIonChange = true;
    try {
      if (this.usuario && this.utils.isAdmin(this.usuario)) {
        await this.initToogleServicios()
        await this.getVersionServidor();
        this.ramaServidor = this.config.config().enviroment;
        this.urlServidor = this.config.config().restUrl;
      }
      this.disableIonChange = false;
    } catch {
      this.disableIonChange = false;
    }
  }

  async initToogleServicios () {
    this.loadingService = true;
    await this.refreshServicios();
    this.loadingService = false;
  }

  async refreshServicios() {
    const serviciosActivos = await this.dataManagementService.getContenedoresActivos(this.usuario.id);
    this.serviciosToogle.forEach((s:any) => {
      s.habilitado = serviciosActivos && serviciosActivos.indexOf(s.nombreContenedor) > 0
    });
  }

  async ionViewWillEnter() {
    this.usuario = await this.dataManagementService.getUsuario();
    if (this.usuario && this.utils.isAdmin(this.usuario)) {
      await this.getVersionServidor();
      this.ramaServidor = this.config.config().enviroment;
      this.urlServidor = this.config.config().restUrl;
    }
  }

  public getInfo(): string {
    let info = 'No hay informacion';
    return info;
  }

  public guardarAjustesUsuario() {
    console.log('ajustesCambiados', this.ajustesCambiados);
  }

  public async getSalidaAjustesUsuario(event: any) {
    if (event as User) {
      await this.dataManagementService.setUsuario(event);
    }
    this.usuario = await this.dataManagementService.getUsuario();
  }

  public getVersionApp(): string {
    return this.config.config().version;
  }

  public async getVersionServidor() {
    try {
      this.versionServidor = await this.dataManagementService.getVersionServidor();
    } catch {
      this.utils.debug('error', 'SettingsPage', 'ngOnInit', 'Error al obtener la version del servidor')
    }
  }

  ionChangeTargetServerFirstTime: boolean = true
  ionChangeTargetServer(event: any, type: string) {
    if (type && type == 'input' && event && event.detail && event.detail.value) {
      this.urlServidor = event.detail.value;
    } else {
      if (event && event.detail && event.detail.value) {
        if (event.detail.value == 'master') {
          this.ramaServidor = 'master';
          this.urlServidor = 'https://puertocho.com/';
          this.config.cambiarUrlPrefix(this.ramaServidor)
          !this.ionChangeTargetServerFirstTime ? this.utils.mostrarAlerta("Servidor cambiado", "Se ha cambiado la url del servidor a " + this.urlServidor): null
          
        } else if (event.detail.value == 'dev') {
          this.ramaServidor = 'dev';
          this.urlServidor = 'https://dev.puertocho.com/';
          this.config.cambiarUrlPrefix(this.ramaServidor);
          !this.ionChangeTargetServerFirstTime ? this.utils.mostrarAlerta("Servidor cambiado", "Se ha cambiado la url del servidor a " + this.urlServidor): null
          
        } else if (event.detail.value == 'custom') {
          this.ramaServidor = 'custom';
        }
      }
    }
    this.ionChangeTargetServerFirstTime = false 
  }

  public cambiarServidorManualmente() {
    this.config.cambiarUrlPrefix(this.ramaServidor, this.urlServidor);
    this.utils.mostrarAlerta("Servidor cambiado", "Se ha cambiado la url del servidor a " + this.urlServidor);
  }

  public noEsServidorCustom(): boolean {
    return this.ramaServidor != 'custom';
  }

  ionChangeToggleServicios(event: any, servicio: any) {
    if (!this.disableIonChange) {
      if (servicio && event && event.detail && event.detail.value) {
        this.loadingService = true;
        this.dataManagementService.setContenedoresActivos(this.usuario.id, servicio.nombreContenedor).finally(async () => {
          await this.refreshServicios();
          this.loadingService = false;
        }) ;
      }
    }
    return null
  }

}