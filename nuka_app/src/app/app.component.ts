import { Component, OnInit } from '@angular/core';
import { AlertController, BackButtonEvent, MenuController, NavController, Platform } from '@ionic/angular';
import { PersistenceService } from './services/persistence.service';
import { AppDataModel } from './app.data.model';
import { UtilsService } from './services/utils.service';
import { App } from '@capacitor/app';
import { Router } from '@angular/router';
import { register } from 'swiper/element/bundle';
import { EventsService } from './services/events.service';
import { GrupoOpciones, OpcionBasica, OpcionesModal, User } from './models/nuka.model';
import { DataManagementService } from './services/data-management.service';
import { ConfigService } from './config/config.service';
import { NotificacionesService } from './services/notificaciones-service.service';

register();

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {
  
  usuario: User | any;
  mostrarSplashGif: boolean = false;
  splashGifTerminado: boolean = false;
  loading: boolean = true;
  habilitarSplashScreen: boolean = false;
  sinConexion: boolean = false;

  constructor(
    public navController: NavController,
    public menu: MenuController,
    public persistance: PersistenceService,
    public appData: AppDataModel,
    public platform: Platform,
    public utils: UtilsService,
    private router: Router,
    private alertCtrl: AlertController,
    public eventsService: EventsService,
    public dataManagementService: DataManagementService,
    public config: ConfigService,
    public notificacionesService: NotificacionesService
  ) {
    this.habilitarSplashScreen = this.config.config().habilitarSplashScreen;

    this.platform.ready().then(() => {
      this.platform.backButton.subscribeWithPriority(-1, async () => {
        await this.backButton();
      });
    });

    this.eventsService.subscribe('habilitarMenu', async (bool) => {
      if (await this.menu.isEnabled() != bool) {
        this.loading = true;
        this.menu.enable(bool);
        if(bool && this.habilitarSplashScreen) {
              this.mostrarSplashGif = true;
              setTimeout( ()=> {
                this.splashGifTerminado = true;
                this.mostrarSplashGif = false;
                setTimeout( ()=> {
                  this.loading = false;
                }, 1900);
              }, 2400);
        } else {
          this.loading = false;
        }
      }
    })

    this.eventsService.subscribe('sin-conexion', (bool) => {
      this.sinConexion = bool;
    });

    this.eventsService.subscribe('actualizarUsario', async (bool) => {
      this.usuario = await this.dataManagementService.getUsuario();
    })
  }

  public getClassSplashDiv() {
    return  this.splashGifTerminado ? 'animated-div': null;
  }

  async backButton(): Promise<void> {
    const rutaActual = this.router.url;
    console.log('rutaActual', rutaActual)
    if(rutaActual == '/home-tabs/status') {
      this.alertExit();

    } else if (rutaActual.includes('/details/')) {
      this.navController.navigateRoot('/home-tabs/chats');

    } else if (rutaActual.includes('/shopping-tabs')) {
      this.navController.navigateRoot('/shopping-tabs');

    } else {
      this.navController.navigateRoot('/home-tabs/status');
    }
    
  }

  async alertExit() {
    const alert = await this.alertCtrl.create({
      header: 'Salir de la aplicacion',
      message: 'Quieres salir de la aplicacion?',
      buttons: [
        {
          text: 'Si',
          role: 'confirm',
          handler: () => { App.exitApp(); }
        },
        {
          text: 'No',
          role: 'cancel'
        }
      ],
    });
    alert.present();
  }

  async ngOnInit(): Promise<void> {
    await this.persistance.createStorage();
    await this.menu.enable(false);

    this.usuario = await this.dataManagementService.getUsuario();
    if (!this.usuario) {
      this.menu.close();
      this.navController.navigateRoot('login');
      this.loading = false;
    } else {
      this.navController.navigateRoot('home-tabs');
    }
  }

  public redirectTo(page: string) {
    this.navController.navigateForward('/' + page);
  }

  ionViewWillEnter() {
    this.usuario = this.dataManagementService.getUsuario();
  }
  
  getImagenUsuario() {
    if (this.usuario && this.usuario.opcionesModal) {
      if (this.usuario && this.usuario?.opcionesModal) {
        let opcionBasica = this.getOpcionBasica(this.usuario?.opcionesModal, 'General', 'FOTO_AJUSTES_USUARIO');
        if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0) {
          return this.appData.PATH_IMG_AJUSTES + opcionBasica.valor[0];
        }
      }
    }
    return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name
  }

  getOpcionBasica(opcionesModal: OpcionesModal, opcionGrupoId: string, opcionBasicaId: string) {
    let grupoOpciones: GrupoOpciones = (opcionesModal.opciones as any).find((go: any) => go.etiqueta == opcionGrupoId);
    let opcionBasica: OpcionBasica = (grupoOpciones.opciones as any)?.find((o: any) => o.id == opcionBasicaId);
    return opcionBasica;
  }
  
}
