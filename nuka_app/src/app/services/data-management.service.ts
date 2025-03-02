import { Injectable } from '@angular/core';
import { PersistenceService } from './persistence.service';
import { RestService } from './rest.service';
import { Apuesta, EstadoMsj, Estrategia, Filtro, GestorCasaApuesta, LoginResponse, NukaChat, NukaMsj, OpcionesModal, ProductRelation, ShoppingList, User } from '../models/nuka.model';
import { HTTP } from '@ionic-native/http/ngx';
import { File } from '@ionic-native/file/ngx';
import { ModalController, NavController, AlertController } from '@ionic/angular';
import { ConfigService } from '../config/config.service';

@Injectable({
  providedIn: 'root'
})
export class DataManagementService {

  constructor(
    public restService: RestService,
    public persistence: PersistenceService,
    public nativeHTTP: HTTP, 
    public file: File,
    public navController: NavController,
    public modalCtrl: ModalController,
    public alertController: AlertController,
    public config: ConfigService,
  ) { }
  
  
  public cambiarNombreMarkdownGenerado(usuarioId: string, idMarkdown: string, nuevoNombre: string, autogenerar :boolean, carpetasSeleccionadas: string[]): Promise<string> {
    return new Promise((resolve, reject) => {
      this.restService.cambiarNombreMarkdownGenerado(usuarioId, idMarkdown, nuevoNombre, autogenerar, carpetasSeleccionadas).then((res: string) => {
        resolve(res);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public guardarArchivoTemporal(usuarioId: string, carpeta: string, file: any): Promise<string> {
    return new Promise((resolve, reject) => {
      this.restService.guardarArchivoTemporal(usuarioId, carpeta, file).then((data: string) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public getContenedoresActivos(usuarioId: string): Promise<string[]> {
    return new Promise((resolve, reject) => {
      this.restService.getContenedoresActivos(usuarioId).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
  public setContenedoresActivos(usuarioId: string, contenedor: string): Promise<string[]> {
    return new Promise((resolve, reject) => {
      this.restService.setContenedoresActivos(usuarioId, contenedor).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }  
  
  public prepararConocimiento(listElementosSeleccionados: any[]): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.prepararConocimiento(listElementosSeleccionados).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public getListProyectos(usuarioId: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.getListProyectos(usuarioId).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
  
  public generarConocimiento(usuarioId: string, idsMarkdown: any[], carpetasSeleccionadas: any[], opciones: any[]): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.generarConocimiento(usuarioId, idsMarkdown, carpetasSeleccionadas, opciones).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public generarCodigo(usuarioId: string, proyectosSeleccionados: string[], nuevoTexto: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.generarCodigo(usuarioId, proyectosSeleccionados, nuevoTexto).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

/*
  public getEstadoGeneradorConocimiento(usuarioId: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.getEstadoGeneradorConocimiento(usuarioId).then((data: any) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
*/

  public getApuestasBet(usuarioId: string): Promise<Apuesta[]> {
    return new Promise((resolve, reject) => {
      this.restService.getApuestasBet(usuarioId).then((data: Apuesta[]) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
  
  public getEstrategias(usuarioId: string): Promise<Estrategia[]> {
    return new Promise((resolve, reject) => {
      this.restService.getEstrategias(usuarioId).then((data: Estrategia[]) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public getGestorCasaApuestasBet(usuarioId: string): Promise<GestorCasaApuesta[]> {
    return new Promise((resolve, reject) => {
      this.restService.getGestorCasaApuestasBet(usuarioId).then((data: GestorCasaApuesta[]) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
  

  public getAjustesBet(usuarioId: string): Promise<OpcionesModal> {
    return new Promise((resolve, reject) => {
      this.restService.getAjustesBet(usuarioId).then((data: OpcionesModal) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
  

    public getSaldoBet(usuarioId: string): Promise<number> {
      return new Promise((resolve, reject) => {
        this.restService.getSaldoBet(usuarioId).then((data: number) => {
          const saldoConDosDecimales = parseFloat(data.toFixed(2));
          resolve(saldoConDosDecimales);
        }).catch((error: any) => {
          reject(error);
        });
      });
    }


  public autonombrarChat(chatId: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.autonombrarChat(chatId).then((data: any) => {
        console.log('data', data)
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public modificarCitaOTarea(eventoParaModificar: EstadoMsj): Promise<EstadoMsj> {
    return new Promise((resolve, reject) => {
      this.restService.modificarCitaOTarea(eventoParaModificar).then((data: EstadoMsj) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public mostrarResultadosBets(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.mostrarResultadosBets().then((data: EstadoMsj) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public usarDirectoriosObsidian(chatId: string, usarDirectoriosObsidian: boolean): Promise<NukaChat> {
    return new Promise((resolve, reject) => {
      this.restService.usarDirectoriosObsidian(chatId, usarDirectoriosObsidian).then((data: NukaChat) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public cambiarIdsDirectorios(chatId: string, ids: string[]): Promise<NukaChat> {
    return new Promise((resolve, reject) => {
      this.restService.cambiarIdsDirectorios(chatId, ids).then((data: NukaChat) => {
        resolve(data);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public actualizarTokenDispositivo(token: string): Promise<boolean> {
      return new Promise(async (resolve, reject) => {
        const usuario = await this.getUsuario();
        let usuarioId = '';
        if (usuario && usuario.id) {
          usuarioId = usuario.id;
        }
        const dispositivo = this.config.config().platform;
        this.restService.actualizarTokenDispositivo(usuarioId, dispositivo, token).then((bool) => {
          resolve(bool);
        }).catch((error) => {
          reject(error);
        });
      });
    }

  public async cerrarSesion() {
    console.log('pal login')
    if (await this.modalCtrl.getTop()) {
      this.modalCtrl.dismiss();
    }
    if (await this.alertController.getTop()) {
      this.alertController.dismiss();
    }

    await this.deleteAllPersistenceData();
    this.navController.navigateRoot('login');
  }

  public getAllPersistenceData(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.persistence.getAllData()
        .then(data => {
          resolve(data);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  public deleteAllPersistenceData(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.persistence.deleteAllData()
        .then(data => {
          resolve(data);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  public getVersionServidor(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.getVersionServidor().then((data: any) => {
        resolve(data.version);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public terminarTareaPorHacer( usuarioId: string, msjEstadoId: string): Promise<EstadoMsj> {
      return new Promise((resolve, reject) => {
        this.restService.terminarTareaPorHacer(usuarioId, msjEstadoId).then((data) => {
          resolve(data);
        }).catch((error) => {
          reject(error);
        });
      });
    }

    public agregarTareaPorHacer( usuarioId: string, msjEstadoId: string): Promise<EstadoMsj> {
      return new Promise((resolve, reject) => {
        this.restService.agregarTareaPorHacer(usuarioId, msjEstadoId).then((data) => {
          resolve(data);
        }).catch((error) => {
          reject(error);
        });
      });
    }

  public añadirEstadoMsj(usuarioId?: string, tipoMsjEstado?: string, fecha?: string, texto?: string,
    imgMsjPath?: string, estado?: string, tiempoRepeticion?: string, usuariosIdsUsername?: string[]): Promise<EstadoMsj[]> {
      return new Promise((resolve, reject) => {
        this.restService.añadirEstadoMsj(usuarioId, tipoMsjEstado, fecha, texto, imgMsjPath, estado, tiempoRepeticion, usuariosIdsUsername).then((data) => {
          resolve(data);
        }).catch((error) => {
          reject(error);
        });
      });
    }

  public cerrarNotificacion(estadoMsjId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.restService.cerrarNotificacion(estadoMsjId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getNotificaciones(usuarioId: string): Promise<EstadoMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getNotificaciones(usuarioId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getEventosYCitas(usuarioId: string, mes: number, anyo: number): Promise<EstadoMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getEventosYCitas(usuarioId, mes, anyo).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getTareas(usuarioId: string): Promise<EstadoMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getTareas(usuarioId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public toogleFavoriteStatus(nukaChatId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.restService.toogleFavoriteStatus(nukaChatId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }
  
  public eliminarNukaChat(nukaChatId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.restService.eliminarNukaChat(nukaChatId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public eliminarNukaMensajes(nukaMsjIds: string[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.restService.eliminarNukaMensajes(nukaMsjIds).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getMessagesBetweenDates(chatId: string, messageIdStart?: string, messageIdEnd?: string, fechaStart?: string, fechaEnd?: string): Promise<NukaMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getMessagesBetweenDates(chatId, messageIdStart, messageIdEnd, fechaStart, fechaEnd).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getNukaChatsByIds(chatIds: string[]): Promise<NukaChat[]> {
    return new Promise((resolve, reject) => {
      this.restService.getNukaChatsByIds(chatIds).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getBusquedaChat(texto: string, usuarioId: string): Promise<NukaMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getBusquedaChat(texto, usuarioId).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public customGet(endpoint: string, params?: any): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.customGet(endpoint, params).then((data) => {
        resolve(data);
      }).catch((error) => {
        console.log(error);
        resolve(null);
      });
    });
  }

  public customPost(endpoint: string, params?: any): Promise<ShoppingList[]> {
    return new Promise((resolve, reject) => {
      this.restService.customPost(endpoint, params).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }
  
  public getShoppingHistory(paginaActual: number, itemPorPagina: number): Promise<ShoppingList[]> {
    return new Promise((resolve, reject) => {
      this.restService.getShoppingHistory(paginaActual, itemPorPagina).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getNumShoppingHistory(): Promise<number> {
    return new Promise((resolve, reject) => {
      this.restService.getNumShoppingHistory().then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getShoppingCart(): Promise<ShoppingList> {
    return new Promise((resolve, reject) => {
      this.restService.getShoppingCart().then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public confirmarProductosSeleccionados(shoppingListId: string, productsMarcados: ProductRelation[]): Promise<ShoppingList> {
    return new Promise((resolve, reject) => {
      this.restService.confirmarProductosSeleccionados(shoppingListId, productsMarcados).then((shoppingList) => {
        resolve(shoppingList);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public test(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.test().then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public async getLocalAudios(): Promise<any> {
    return await this.persistence.getValue('localAudios');
  }

  public async setLocalAudios(value: any): Promise<boolean> {
    return await this.persistence.setValue('localAudios', value);
  }

  public async addLocalAudio(id: string): Promise<any> {
    let localAudios: [] = await this.getLocalAudios();
    //localAudios.find()

    return await this.persistence.getValue('localAudios');
  }

  public async setLocalAudio(value: any): Promise<boolean> {
    return await this.persistence.setValue('localAudios', value);
  }

  public async getValue(key: string): Promise<any> {
    return await this.persistence.getValue(key);
  }

  public async setValue(key: string, value: any): Promise<boolean> {
    return await this.persistence.setValue(key, value);
  }

  public async getUsuario(): Promise<User> {
    return await this.persistence.getValue('usuario');
  }

  public async setUsuario(value: User | null): Promise<boolean> {
    return await this.persistence.setValue('usuario', value);
  }

  public async getToken(): Promise<string> {
    return await this.persistence.getToken();
  }

  public async setToken(value: String): Promise<boolean> {
    return await this.persistence.setToken(value);
  }

  public async getInfoChatGeneral(): Promise<NukaChat> {
    return await this.persistence.getValue('infoChatGeneral');
  }

  public async setInfoChatGeneral(value: User): Promise<boolean> {
    return await this.persistence.setValue('infoChatGeneral', value);
  }

  public async getLocalMessages(): Promise<NukaMsj[]> {
    return await this.persistence.getValue('localMessages');
  }

  public async setLocalMessages(value: NukaMsj[]): Promise<boolean> {
    return await this.persistence.setValue('localMessages', value);
  }

  public async getLocalChats(): Promise<NukaChat[]> {
    let localChats = await this.persistence.getValue('localChats')
    return localChats? localChats: [];
  }

  public async setLocalChats(value: NukaChat[]): Promise<boolean> {
    return await this.persistence.setValue('localChats', value);
  }

  public nukaMsjPeticion(nukaMsjPeticion: NukaMsj, contenidoFiles: any): Promise<NukaMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.nukaMsjPeticion(nukaMsjPeticion, contenidoFiles).then((nukaMsjRespuesta) => {
        resolve(nukaMsjRespuesta);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  testAudio() {
    fetch('https://puertocho.com/api/getAudio')
    .then(response => {
      if (!response.ok) {
        throw new Error('La solicitud no fue exitosa.');
      }
      return response.blob();
    })
    .then(async audioBlob => {
      const audio = new Audio(URL.createObjectURL(audioBlob));
      await audio.play();
    })
    .catch(error => {
      console.error('Error en la solicitud:', error);
    });
  }

  public getResource(id: string, tipo: string): Promise<Blob | null> {
    return new Promise(async (resolve) => {
      this.restService.getResource(id, tipo, await this.getToken()).then((blob) => {
        blob? resolve(blob): resolve(null);
      }).catch((_) => {
        resolve(null);
      });
    });
  }

  public async playAudio(): Promise<void> {
    const blob = await this.restService.getResource('1690222453518_srs7yfjr0tqdhymy7x8lp523', 'audioWav', await this.getToken());
    if (blob) {
      const audio = new Audio(URL.createObjectURL(blob));
      await audio.play();
    }
/*
    const blob = await this.restService.getAudioBlob();
    const audioUrl = URL.createObjectURL(blob);
    const audioElement = new Audio(audioUrl);

    try {
      await audioElement.play();
    } catch (error) {
      console.error('Error al reproducir el audio:', error);
    }
*/
  }

  public cancelHttpPeticion(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restService.cancelHttpPeticion().then((data) => {
          console.log('cancelHttpPeticion data', data);
        resolve(data);
      }).catch((error) => {
        console.log('cancelHttpPeticion data', error);

        reject(error);
      });
    });
  }

  public login(userNick: string, password: string): Promise<LoginResponse> {
    return new Promise((resolve, reject) => {
      this.restService.login(userNick, password).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public actualizarToken(userNick: string, encodedPassword: string): Promise<LoginResponse> {
    console.log(userNick);
    console.log(encodedPassword);
    return new Promise((resolve, reject) => {
      this.restService.actualizarToken(userNick, encodedPassword).then((data) => {
        resolve(data);
      }).catch((error) => {
        reject(error);
      });
    });
  }

  public getNukaChats(usuarioId: string, filtros?: Filtro[]): Promise<NukaChat[]> {
    return new Promise((resolve, reject) => {
      this.restService.getNukaChats(usuarioId, filtros).then((chats: NukaChat[] | PromiseLike<NukaChat[]>) => {
        resolve(chats);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public getMessages(chatIds: string[], numMensajes?: number, fecha?: string): Promise<NukaMsj[]> {
    return new Promise((resolve, reject) => {
      this.restService.getMessages(chatIds, numMensajes, fecha).then(async (messages: NukaMsj[]) => {
        /*
        let localMessages: NukaMsj[] = await this.getLocalMessages();
        if (messages) {
          messages.push(...localMessages);   
          let uniqueMessages: NukaMsj[] = [];
          messages.forEach(message => {
            const messageId = message.id;
            if (message && messageId && !uniqueMessages.some(m => m.id == message.id)) {
              uniqueMessages.push(message);
            }
          });

          messages = uniqueMessages;
          const bool = this.setLocalMessages(messages);
          console.log(bool, messages);
          
        }
        */
        resolve(messages);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }

  public agregarChat(summary: string, usuarioId: string): Promise<NukaChat> {
    return new Promise((resolve, reject) => {
      this.restService.agregarChat(summary, usuarioId).then((nukaChat: NukaChat | PromiseLike<NukaChat>) => {
        resolve(nukaChat);
      }).catch((error: any) => {
        reject(error);
      });
    });
  }
}
