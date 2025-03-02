import { Injectable } from '@angular/core';
import { ConfigService } from '../config/config.service';
import { AlertController, Platform, ToastController } from '@ionic/angular';
import { GrupoOpciones, NukaChat, NukaMsj, OpcionBasica, OpcionesModal, User } from '../models/nuka.model';
import { DateTimeFormatOptions } from 'luxon';
import { AppDataModel } from '../app.data.model';
import { DataManagementService } from './data-management.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {

  constructor(
    public config: ConfigService,
    public toastController: ToastController,
    public platform: Platform,
    public alertController: AlertController,
    public appData: AppDataModel,
    public router: Router
  ) { }

  isPhone(): boolean {
    let platform = this.config.config().platform;
    return platform == 'PHONE';
  }

  public primeraMayuscula(nombre: string) {
    return nombre.charAt(0).toUpperCase() + nombre.slice(1);
  }
  
  getRandomInt(max: number) {
    return Math.floor(Math.random() * max);
  }
  
  isAdmin(usuario: User) {
    return usuario && usuario.permission == 'ADMIN';
  }

  isToday(fecha: any) {
    const fechaHoy = new Date(Date.now());
    const fechaDate = new Date(fecha);
    return fechaDate.getUTCFullYear() === fechaHoy.getUTCFullYear() &&
    fechaDate.getMonth() === fechaHoy.getMonth() &&
    fechaDate.getDate() === fechaHoy.getDate();
  }

  isTomorrow(fecha: any) {
    const fechaHoy = new Date(Date.now());
    const fechaDate = new Date(fecha);
    return fechaDate.getUTCFullYear() === fechaHoy.getUTCFullYear() &&
    fechaDate.getMonth() === fechaHoy.getMonth() &&
    fechaDate.getDate() === (fechaHoy.getDate() + 1); 
  }

  isThisMonth(mes?: any , anyo?: any ) {
    const fechaHoy = new Date(Date.now());
    return (anyo === fechaHoy.getUTCFullYear()) && (mes === fechaHoy.getMonth() + 1);
  }

  async presentToast(texto: string, 
    position?: 'top' | 'middle' | 'bottom', 
    color?: 'primary' | 'secondary' | 'tertiary' | 'success' | 'warning' | 'danger' | 'light' | 'medium' | 'dark',
    duration?: number): Promise<any> {
       const toast = await this.toastController.create({
      message: texto,
      duration: duration ? duration : 1500,
      position: position ? position: 'middle',
      color: color ? color: 'primary'
    });
    await toast.present();
    return toast;
  }

  public generarIdAleatorio(longitud: number): string {
    const caracteres = 'abcdefghijklmnopqrstuvwxyz0123456789';
    let resultado = '';
  
    for (let i = 0; i < longitud; i++) {
      const indice = Math.floor(Math.random() * caracteres.length);
      resultado += caracteres.charAt(indice);
    }
  
    return resultado;
  }

  convertBase64ToArrayBuffer(base64: string | any): ArrayBuffer {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  convertBlobToArrayBuffer(blob: Blob): Promise<ArrayBuffer> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as ArrayBuffer);
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(blob);
    });
  }

  convertBase64ToBlob(base64: string, mimeType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: mimeType });
  }

  public convertirAudio(audioElement: HTMLAudioElement, name: string): Promise<File | null> {
    return new Promise((resolve) => {
      const audioUrl = audioElement.src;
      const xhr = new XMLHttpRequest();
      xhr.open("GET", audioUrl, true);
      xhr.responseType = "blob";
      xhr.onload = () => {
        if (xhr.status === 200) {
          const blob = xhr.response;
          const file = new File([blob], name, { type: blob.type });
          resolve(file);
        } else {
          console.log(new Error("No se pudo obtener el archivo de audio."));
          resolve(null);
        }
      };
      xhr.onerror = () => {
        console.log(new Error("Error al obtener el archivo de audio."));
        resolve(null);
      };
      xhr.send();
    });
  }

  public isDesktop() {
    console.log('this.platform', this.platform.platforms())
    return this.platform.is('desktop')
  }

  public getTipoPantalla(): string {
    if (screen.width < 1024) {
      return 'movil'
    } else if (screen.width < 1280) {
      return 'tablet'
    } else {
      return 'monitor'
    }
  }

  public isAndroid() {
    return this.platform.is('android') 
    || this.platform.is('mobile') 
    || this.platform.is('mobileweb') 
    || this.platform.is('tablet')
  }

  public getColorCategoriaListaCompra(categoria: string | undefined) {
    switch (categoria) {
      case 'Lacteos':
        return '#6AB312';
      case 'Bebidas':
        return '#FF0901';
      case 'Carne':
        return '#19DFFC';
      case 'Congelados':
        return '#FFD417';
      case 'Higiene':
        return '#23FC5A';
      case 'Limpieza':
        return '#FF7DBE';
      case 'Snacks':
        return '#FCE13D';
      case 'Otros':
        return '#B3412B';
      case 'Marcado':
        return '#CCC';
      case 'normal':
        return '#000'
    }
    return "#CCC";
  }

  async mostrarAlertaActualizarToken() {
    await this.alertController.dismiss();
    const botones = [
      { text: "Salir", role: "salir" },
      { text: "Enviar", role: "enviar" }];
    const alert = await this.alertController.create({
      header: 'Fingeprint no disponible. Introduce tu contraseña',
      buttons: botones,
      inputs: [
        {
          type: 'password',
          placeholder: 'Contraseña de usuario',
        }
      ],
      backdropDismiss : false
    });

    await alert.present();
    return alert;
  }

  public async mostrarAlerta(header?: string, message?: string, buttons?: string[] | any[], forceSelect?: boolean) {
    const alert = await this.alertController.create({
      header: header? header: 'Alerta',
      message: message? message: '¡Hola! Esto es el contenido de la alerta.',
      buttons: buttons? buttons: ['OK'],
      backdropDismiss : forceSelect? false: true,
    });
    await alert.present();
    return alert;
  }

  public tienePermiso(tab: any, user: User): boolean {
    if (tab.permiso != 'ANY') {
      if (tab.permiso == 'ADMIN') {
        return user?.permission == 'ADMIN';
      } else if (tab.permiso == 'USER') {
        return user?.permission == ('ADMIN') || user?.permission == ('USER');
      }  
    }
    return user != null;
  }

  public capitalizarPrimeraLetra(str: string) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  public obtenerNombreMes(numero: number): string | null {
    // El número está fuera del rango válido de meses
    if (numero < 0 || numero > 11) { return null; }
    const fecha = new Date();
    fecha.setMonth(numero);
    const opciones: DateTimeFormatOptions = { month: 'long' };
    const nombreMes = fecha.toLocaleDateString(undefined, opciones);
    return this.capitalizarPrimeraLetra(nombreMes);
  }

  public obtenerNumeroMes(nombre: string | null): number | null {
    const nombreLowerCase = nombre?.toLowerCase().replace(' ', '');
    switch (nombreLowerCase) {
        case 'enero':
        case 'january': {
            return 1;
        }

        case 'febrero':
        case 'february': {
            return 2;
        }

        case 'marzo':
        case 'march': {
            return 3;
        }

        case 'abril':
        case 'april': {
            return 4;
        }

        case 'mayo':
        case 'may': {
            return 5;
        }

        case 'junio':
        case 'june': {
            return 6;
        }

        case 'julio':
        case 'july': {
            return 7;
        }

        case 'agosto':
        case 'august': {
            return 8;
        }

        case 'septiembre':
        case 'september': {
            return 9;
        }

        case 'octubre':
        case 'october': {
            return 10;
        }

        case 'noviembre':
        case 'november': {
            return 11;
        }

        case 'diciembre':
        case 'december': {
            return 12;
        }

        default: {
            return null;
        }
    }
}

  public obtenerTiempoTranscurrido(timestamp: number): string {
    const fechaActual = new Date();
    const fechaTimestamp = new Date(timestamp);
    const diferenciaMilisegundos = fechaActual.getTime() - fechaTimestamp.getTime();
    const diferenciaDias = Math.floor(diferenciaMilisegundos / (1000 * 60 * 60 * 24));

    if (diferenciaDias === 0 || diferenciaDias === 1) {
      if (fechaTimestamp.getDate() === fechaActual.getDate() - 1) {
        return 'Ayer'
      } else {
        return 'Hoy'
      }
    } else if (diferenciaDias > 0) {
      return `Hace ${diferenciaDias} día${diferenciaDias > 1 ? 's' : ''}`;
    } else {
      return 'Hoy'
    }
  }
  
  public getOpcionBasicaById(opcionesModal: OpcionesModal, id: string): undefined | OpcionBasica {
    for (const grupo of opcionesModal.opciones || []) {
      const normalizedId = grupo.etiqueta?.toUpperCase();
      if (id.split('_')[0] === normalizedId) {
        return grupo.opciones?.find(o => o.id === id);
      }
    }
    return undefined;
  }

  public getOpcionBasica(opcionesModal: OpcionesModal, opcionGrupoId: string, opcionBasicaId: string): OpcionBasica {
    if (opcionesModal) {
      let grupoOpciones: GrupoOpciones = (opcionesModal.opciones as any).find((go: any) => go.etiqueta == opcionGrupoId);
      let opcionBasica: OpcionBasica = (grupoOpciones.opciones as any)?.find((o: any) => o.id == opcionBasicaId);
      return opcionBasica;
    }
    return null as any;
  }

  public getImagenUsuario(usuario: User) {
    if (usuario && usuario.opcionesModal) {
      if (usuario && usuario?.opcionesModal) {
        let opcionBasica = this.getOpcionBasica(usuario?.opcionesModal, 'General', 'FOTO_AJUSTES_USUARIO');
        if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0) {
          return this.appData.PATH_IMG_AJUSTES + opcionBasica.valor[0];
        }
      }
    }
    return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name
  }

  public getImagenChat(chat: NukaChat) {
    if (chat && chat.opcionesModal) {
      if (chat && chat?.opcionesModal) {
        let opcionBasica = this.getOpcionBasica(chat?.opcionesModal, 'GENERAL', 'GENERAL_FOTO_CHAT');
        if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0) {
          return this.appData.PATH_IMG_AJUSTES + opcionBasica.valor[0];
        }
      }
    }
    return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name
  }

  public getValueOpcion(opcionBasica: OpcionBasica) {
    if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0){
      return opcionBasica.valor[0];
    }
    return null;
  }

  public getDatosOpcion(opcionBasica: OpcionBasica) {
    if (opcionBasica && opcionBasica.datos && opcionBasica.datos.length > 0){
      return opcionBasica.datos;
    }
    return [];
  }

  public getDatoOpcion(opcionBasica: OpcionBasica) {
    if (opcionBasica && opcionBasica.datos && opcionBasica.datos.length > 0){
      return opcionBasica.datos[0];
    }
    return [];
  }

  public getValuesOpcion(opcionBasica: OpcionBasica) {
      if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0) {
        return opcionBasica.valor;
      }
      return [];
  }

  public getEtiquetaOpcion(opcionBasica: OpcionBasica) {
    return opcionBasica.etiqueta;
  }

  public getPathImage(name: string) {
    return this.appData.PATH_IMG_AJUSTES + name;
  }

  getImagenPath(opcionBasica: OpcionBasica): string {
    if (opcionBasica && opcionBasica.valor && opcionBasica.valor?.length > 0){
        const bool = this.appData.IMG_INFO.some(i => i.name == (opcionBasica as any).valor[0]);
        if (bool) {
          return this.appData.PATH_IMG_AJUSTES + (opcionBasica as any).valor[0];
        }
    }
    return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name;
  }
  
  async getResourceAudio(message: NukaMsj, dataManagementService: DataManagementService): Promise<HTMLAudioElement |any> {
    if (message && message.id && message.needAudio && message.audioResource) {
      const blob = await dataManagementService.getResource(message.id, message.audioResource);
      if (blob) {
        const audioUrl = URL.createObjectURL(blob);
        return new Audio(audioUrl);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public mostrarImagenChat(chat: NukaChat | any) {
    let opcionBasica = this.getOpcionBasica(chat.opcionesModal, 'GENERAL', 'GENERAL_FOTO_CHAT');
    let value = this.getValueOpcion(opcionBasica);
    return value && value.length > 0;
  }

  public traducirMes(mes: string): string | null {
    const nombreLowerCase = mes.toLowerCase().replace(' ', '');
    switch (nombreLowerCase) {
        case 'enero': {
            return 'January';
        }

        case 'febrero': {
            return 'February';
        }

        case 'marzo': {
            return 'March';
        }

        case 'abril': {
            return 'April';
        }

        case 'mayo': {
            return 'May';
        }

        case 'junio': {
            return 'June';
        }

        case 'julio': {
            return 'July';
        }

        case 'agosto': {
            return 'August';
        }

        case 'septiembre': {
            return 'September';
        }

        case 'octubre': {
            return 'October';
        }

        case 'noviembre': {
            return 'November';
        }

        case 'diciembre': {
            return 'December';
        }

        default: {
            return 'January';
        }
    }
  }

  public getCurrentPage() {
    const currentUrl = this.router.url;
    return currentUrl;
  }

  reloadPage(TuRutaActual: string) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate([TuRutaActual]);
    }); 
  }

  debug(tipo: string, nombreDeComponente: string, nombreDeMetodo: string, mensanje: string) {
    /* Ejemplo:
      this.utils.debug('error', 'SettingsPage', 'testNotificaciones', 'prueba de test debug');
      this.utils.debug('error', '', '', '')
    */
    const texto = nombreDeComponente + " : " + nombreDeMetodo + " : " + mensanje;
    if (tipo == 'log') {
      console.log(texto);
    } else if (tipo == 'warn') {
      console.warn(texto)
    } else {
      console.error(texto)
    }
  }

  debugClean() {
    console.clear()
  }
}
