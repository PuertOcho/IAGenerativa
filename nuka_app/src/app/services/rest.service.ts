import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from '../config/config.service';
import { EventsService } from './events.service';
import { UtilsService } from './utils.service';
import { WsAbstractService } from './ws-abstract.service';
import { Apuesta, Bets, EstadoMsj, Estrategia, Filtro, GestorCasaApuesta, LoginResponse, NukaChat, NukaMsj, OpcionesModal, Product, ProductRelation, ShoppingList, User } from '../models/nuka.model';
import { Media, MediaObject } from '@ionic-native/media/ngx';
import * as base64Arraybuffer from 'base64-arraybuffer';
import { Filesystem, Directory, Encoding } from '@capacitor/filesystem';
import { DataManagementService } from './data-management.service';
import { PersistenceService } from './persistence.service';

@Injectable({
  providedIn: 'root'
})
export class RestService extends WsAbstractService {

  constructor(
    public override http: HttpClient,
    public override config: ConfigService,
    public utils: UtilsService,
    public override events: EventsService,
    public media: Media,
    public override persistence: PersistenceService,
  ) {
    super(http, config, utils, events, persistence);
  }
  
  public actualizarTokenDispositivo(usuarioId: string, dispositivo: string, token: string): Promise<boolean> {
      const fd = new FormData();
      usuarioId != null ? fd.append('usuarioId', String(usuarioId)): null;
      dispositivo != null ? fd.append('dispositivo', String(dispositivo)): null;
      token != null ? fd.append('token', String(token)): null;
      
      return this.makePostRequest('actualizarTokenDispositivo', fd, 300000).then(async (res: boolean) => {
        if (res) {
          return Promise.resolve(res);
        } else {
          return Promise.reject('Error ws nukaMsjPeticion');
        }
      }).catch((error) => {
        return Promise.reject(error);
      });
  }
  
  public async guardarArchivoTemporal(usuarioId: string, carpeta: string, file: any): Promise<string> {
    const fd = new FormData();
    const blob = this.utils.convertBase64ToBlob(file.data, file.mimeType);

    fd.append('usuarioId', usuarioId);
    fd.append('carpeta', carpeta);
    fd.append('file', blob, file.name);

    return this.makePostRequest('guardarArchivoTemporal', fd, 300000).then((res: any) => {
      console.log('res', res)
      if (res?.path) {
          return res.path;
      }
      return Promise.reject('Error ws guardarArchivoTemporal');
    }).catch((error) => {
        return Promise.reject(error);
    });
  }


  public cambiarNombreMarkdownGenerado(usuarioId: string, idMarkdown: string, nuevoNombre: string, autogenerar: boolean, carpetasSeleccionadas: string[]): Promise<any> {
    const fd = new FormData();
    fd.append('usuarioId', usuarioId);
    fd.append('idMarkdown', idMarkdown);
    fd.append('nuevoNombre', nuevoNombre);
    fd.append('autogenerar',  autogenerar.toString());
    fd.append('carpetasSeleccionadasJson', JSON.stringify(carpetasSeleccionadas));

    return this.makePostRequest('cambiarNombreMarkdownGenerado', fd)
        .then((res: any) => {
          console.log('cambiarNombreMarkdownGenerado res', res);
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: cambiarNombreMarkdownGenerado');
            }
        })
        .catch((error) => {
            console.error('error', error);
            return Promise.reject(error);
        });
  }


  public prepararConocimiento(listElementosSeleccionados: any[]): Promise<any> {
    const fd = new FormData();
    fd.append('elementosSeleccionadosJson', JSON.stringify(listElementosSeleccionados));

     return this.makePostRequest('prepararConocimiento', fd, 2400000)
        .then((res: any) => {
          console.log('prepararConocimiento res', res);
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: prepararConocimiento');
            }
        })
        .catch((error) => {
            console.error('error', error);
            return Promise.reject(error);
        });
  }

  public generarConocimiento(usuarioId: string, idsMarkdown: any[], carpetasSeleccionadas: any[], opciones: any[]): Promise<any> {
    const payload = {
        usuarioId: usuarioId,
        idsMarkdown: idsMarkdown,
        carpetasSeleccionadas: carpetasSeleccionadas,
        opciones: opciones
    };
     return this.makePostRequest('generarConocimiento', payload, 2400000)
        .then((res: any) => {
          console.log('generarConocimiento res', res);
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: generarConocimiento');
            }
        })
        .catch((error) => {
            console.error('error', error);
            return Promise.reject(error);
        });
  }

  public generarCodigo(usuarioId: string, proyectosSeleccionados: string[], nuevoTexto: string): Promise<any> {
    const payload = {
        usuarioId: usuarioId,
        proyectosSeleccionados: proyectosSeleccionados,
        nuevoTexto: nuevoTexto
      };
     return this.makePostRequest('generarCodigo', payload, 2400000)
        .then((res: any) => {
          console.log('generarCodigo res', res);
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: generarCodigo');
            }
        })
        .catch((error) => {
            console.error('error', error);
            return Promise.reject(error);
        });
  }

  public getListProyectos(usuarioId: string): Promise<any> {
    let params: any = {};
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getProyectos', params)
        .then((res: any) => {
          console.log("rest getProyectos: ", res)
            if (res && res.proyectos) {
              try {
                  // Convierte el string en un array de strings
                  let proyectosString = res.proyectos.replace(/[\[\]]/g, '').replace(/'/g, '');
                  let proyectosArray = proyectosString.split(',').map((p:any) => p.trim());
                  return Promise.resolve(proyectosArray);
              } catch (error) {
                  return Promise.reject('Error al parsear los proyectos');
              }
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getAjustesBet(usuarioId: string): Promise<OpcionesModal> {
    let params: any = {};
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getAjustesBet', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
    }

  public getEstrategias(usuarioId: string): Promise<Estrategia[]> {
    let params: any = {};
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getEstrategias', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getSaldoBet(usuarioId: string): Promise<number> {
    let params: any = {};
    params['usuarioId'] = usuarioId;
    return this.makeGetRequest('getSaldoBet', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getApuestasBet(usuarioId: string): Promise<Apuesta[]> {
    let params: any = {};
    params['usuarioId'] = usuarioId;
    return this.makeGetRequest('getApuestasBet', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getEncuentrosBet(ids: string[]): Promise<Bets[]> {
    let params: any = {};
    params['ids'] = ids;
    return this.makeGetRequest('getEncuentrosBet', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getGestorCasaApuestasBet(usuarioId: string): Promise<GestorCasaApuesta[]> {
    let params: any = {};
    params['usuarioId'] = usuarioId;
    return this.makeGetRequest('getGestorCasaApuestasBet', params)
        .then((res: any) => {
            if (res) {
                return Promise.resolve(res);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public cambiarIdsDirectorios(chatId: string, ids: string[]): Promise<NukaChat> {
    const fd = new FormData();
    fd.append('chatId', chatId)
    fd.append('ids', String(ids))

    return this.makePostRequest('cambiarIdsDirectorios', fd).then((res: NukaChat) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws cambiarIdsDirectorios');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public usarDirectoriosObsidian(chatId: string, usarDirectoriosObsidian: boolean): Promise<NukaChat> {
    const fd = new FormData();
    fd.append('chatId', chatId);
    fd.append('usarDirectoriosObsidian', String(usarDirectoriosObsidian));

    return this.makePostRequest('usarDirectoriosObsidian', fd).then((res: NukaChat) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws usarDirectoriosObsidian');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public autonombrarChat(chatId: string): Promise<any> {
    let params: any = {};
    params['chatId'] = chatId;

    return this.makeGetRequest('autonombrarChat', params)
        .then((res: any) => {
            console.log('autonombrarChat', res);
            if (res && res.summary) { // Asegúrate de que estés buscando la propiedad correcta
                return Promise.resolve(res.summary);
            } else {
                return Promise.reject('Error: No se recibió un resumen válido');
            }
        })
        .catch((error) => {
            console.log('error', error);
            return Promise.reject(error);
        });
  }

  public getContenedoresActivos(usuarioId: string): Promise<any> {
    let params: any =  [];
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getContenedoresActivos', params).then((res: any) => {
      console.log('getContenedoresActivos', res);
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getContenedoresActivos');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }
  public setContenedoresActivos(usuarioId: string, contenedor: string): Promise<any> {
    let params: any =  [];
    params['usuarioId'] = usuarioId;
    params['contenedor'] = contenedor;

    return this.makeGetRequest('setContenedoresActivos', params).then((res: any) => {
      console.log('REST setContenedoresActivos', res);
      if (res && res.response) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws setContenedoresActivos');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }
  
  public getVersionServidor(): Promise<any> {
    return this.makeGetRequest('getVersionServidor', null).then((res: any) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getVersionServidor');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public cerrarNotificacion(estadoMsjId: string): Promise<boolean> {
    let params: any =  []; 
    params['estadoMsjId'] = estadoMsjId;
    return this.makeGetRequest('cerrarNotificacion', params).then((res: boolean) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getNotificaciones');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getNotificaciones(usuarioId: string): Promise<EstadoMsj[]> {
    let params: any =  []; 
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getNotificaciones', params).then((res: EstadoMsj[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getNotificaciones');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public agregarTareaPorHacer(usuarioId: string, msjEstadoId: string): Promise<EstadoMsj> {
      const fd = new FormData();
      fd.append('usuarioId', String(usuarioId))
      fd.append('msjEstadoId', String(msjEstadoId));
      return this.makePostRequest('agregarTareaPorHacer', fd).then((msjs: EstadoMsj) => {
        if (msjs) {
          return Promise.resolve(msjs);
        } else {
          return Promise.reject('Error ws agregarTareaPorHacer');
        } 
      }).catch((error) => {
        return Promise.reject(error);
      });
    }

  public terminarTareaPorHacer(usuarioId: string, msjEstadoId: string): Promise<EstadoMsj> {
      const fd = new FormData();
      fd.append('usuarioId', String(usuarioId))
      fd.append('msjEstadoId', String(msjEstadoId));

      return this.makePostRequest('terminarTareaPorHacer', fd).then((msj: EstadoMsj) => {
        if (msj) {
          return Promise.resolve(msj);
        } else {
          return Promise.reject('Error ws terminarTareaPorHacer');
        } 
      }).catch((error) => {
        return Promise.reject(error);
      });
  }

  public añadirEstadoMsj(usuarioId?: string, tipoMsjEstado?: string, fecha?: string, texto?: string,
    imgMsjPath?: string, estado?: string, tiempoRepeticion?: string, usuariosIdsUsername?: string[]): Promise<EstadoMsj[]> {
      const fd = new FormData();
      usuarioId != null ? fd.append('usuarioId', String(usuarioId)): null;
      usuariosIdsUsername != null ? fd.append('usuariosIdsUsername', JSON.stringify(usuariosIdsUsername)): null;
      tipoMsjEstado != null ? fd.append('tipoMsjEstado', String(tipoMsjEstado)): null;
      fecha != null ? fd.append('fecha', String(fecha)): null;
      texto != null ? fd.append('texto', String(texto)): null;
      imgMsjPath != null ? fd.append('imgMsjPath', String(imgMsjPath)): null;
      estado != null ? fd.append('estado', String(estado)): null;
      tiempoRepeticion != null ? fd.append('tiempoRepeticion', String(tiempoRepeticion)): null;

      return this.makePostRequest('añadirEstadoMsj', fd).then((msjs: EstadoMsj[]) => {
        if (msjs) {
          return Promise.resolve(msjs);
        } else {
          return Promise.reject('Error ws añadirEstadoMsj');
        }
      }).catch((error) => {
        return Promise.reject(error);
      });
  }

  public getEventosYCitas(usuarioId: string, mes: number, anyo: number): Promise<EstadoMsj[]> {
    let params: any =  []; 
    params['usuarioId'] = usuarioId;
    params['mes'] = mes;
    params['anyo'] = anyo;
    
    return this.makeGetRequest('getEventosYCitas', params).then((res: EstadoMsj[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getEventosYCitas');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getTareas(usuarioId: string): Promise<EstadoMsj[]> {
    let params: any =  []; 
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getTareas', params).then((res: EstadoMsj[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getTareas');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public toogleFavoriteStatus(nukaChatId: string): Promise<boolean> {
    let params: any =  []; 
    params['nukaChatId'] = nukaChatId;

    return this.makeGetRequest('toogleFavoriteStatus', params).then((res: boolean) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws toogleFavoriteStatus');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }
  
  public eliminarNukaChat(nukaChatId: string): Promise<boolean> {
    let params: any =  []; 
    params['nukaChatId'] = nukaChatId;

    return this.makeGetRequest('eliminarNukaChat', params).then((res: boolean) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws eliminarNukaChat');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public eliminarNukaMensajes(nukaMsjIds: string[]): Promise<boolean> {
    let body: any = nukaMsjIds;
    return this.makePostRequest('eliminarNukaMensajes', body).then((res: boolean) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws eliminarNukaMensajes');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getMessagesBetweenDates(chatId: string, messageIdStart?: string, messageIdEnd?: string, fechaStart?: string, fechaEnd?: string): Promise<NukaMsj[]> {
    const fd = new FormData();
    fd.append('chatId', String(chatId))
    messageIdStart? fd.append('messageIdStart', String(messageIdStart)): null;
    messageIdEnd? fd.append('messageIdEnd', String(messageIdEnd)): null;
    fechaStart? fd.append('fechaStart', String(fechaStart)): null;
    fechaEnd? fd.append('fechaEnd', String(fechaEnd)): null;

    return this.makePostRequest('getMessagesBetweenDates', fd).then((msjs: NukaMsj[]) => {
      if (msjs) {
        return Promise.resolve(msjs);
      } else {
        return Promise.reject('Error ws getMessagesBetweenDates');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getNukaChatsByIds(chatIds: string[]): Promise<NukaChat[]> {
    let body: any = chatIds;
    return this.makePostRequest('getNukaChatsByIds', body).then((chats: NukaChat[]) => {
      if (chats) {
        return Promise.resolve(chats);
      } else {
        return Promise.reject('Error ws getNukaChatsByIds');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getBusquedaChat(texto: string, usuarioId: string): Promise<NukaMsj[]> {
    let params: any =  []; 
    params['texto'] = texto;
    params['usuarioId'] = usuarioId;

    return this.makeGetRequest('getBusquedaChat', params).then((res: NukaMsj[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getBusquedaChat');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public customGet(endpoint: string, params?: any): Promise<any> {
    return this.makeGetRequest(endpoint, params).then((res: any) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws ' + endpoint);
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public customPost(endpoint: string, params?: any): Promise<any> {
    return this.makePostRequest(endpoint, params).then((res: any) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws ' + endpoint);
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }  

  public getShoppingHistory(paginaActual: number, itemPorPagina: number): Promise<ShoppingList[]> {
    let params: any =  []; 
    params['paginaActual'] = paginaActual;
    params['itemPorPagina'] = itemPorPagina;

    return this.makeGetRequest('getShoppingHistory', params).then((res: ShoppingList[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getShoppingHistory');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getNumShoppingHistory(): Promise<number> {
    return this.makeGetRequest('getNumShoppingHistory', null).then((res: number) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getNumShoppingHistory');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getShoppingCart(): Promise<ShoppingList> {
    return this.makeGetRequest('getShoppingCart', null).then((res: ShoppingList) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getShoppingCart');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public async confirmarProductosSeleccionados(shoppingListId: string, productsMarcados: ProductRelation[]): Promise<ShoppingList> {

    await productsMarcados.forEach(pm => {
      delete pm.productInfo?.embedding;
      delete pm.productInfo?.aliasProducto;
    });

    //console.log('productsMarcados', productsMarcados)
    const productsMarcadosJson = JSON.stringify(productsMarcados);

    let params: any =  []; 
    params['shoppingListId'] = shoppingListId;
    params['productsMarcadosJson'] = productsMarcadosJson;

    return this.makeGetRequest('confirmarProductosSeleccionados', params).then((res: ShoppingList) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws confirmarProductosSeleccionados');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public test(): Promise<string> {
    return this.makeGetRequest('test', null).then((res: string) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws getVersion');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getMessages(chatIds: string[], numMensajes?: number, fecha?: string): Promise<NukaMsj[]> {

    const fd = new FormData();
    fd.append('chatIds', String(chatIds));
    fd.append('numMensajes', String(numMensajes));
    fd.append('fecha', String(fecha));

    return this.makePostRequest('getMessages', fd).then((chats: NukaMsj[]) => {
      if (chats) {
        return Promise.resolve(chats);
      } else {
        return Promise.reject('Error ws getMessages');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public getNukaChats(usuarioId: string, filtros?: Filtro[]): Promise<NukaChat[]> {
    const fd = new FormData();
    fd.append('usuarioId', usuarioId);

    if (filtros && filtros.length > 0) {
      fd.append('filtroJson', JSON.stringify(filtros));
    }

    return this.makePostRequest('getNukaChats', fd).then((chats: NukaChat[]) => {
      if (chats) {
        return Promise.resolve(chats);
      } else {
        return Promise.reject('Error ws getNukaChats');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }
  
  public cancelHttpPeticion(): Promise<any> {
    return this.cancelRest().then((res: LoginResponse | any)  => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws cancelHttpPeticion');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public login(userNick: string, password: string): Promise<LoginResponse> {
    const fd = new FormData();
    fd.append('userNick', userNick);
    fd.append('password', password);
    
    return this.makePostRequest('login', fd).then((res: LoginResponse | any)  => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws login');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public actualizarToken(userNick: string, encodedPassword: string): Promise<LoginResponse> {
    const fd = new FormData();
    fd.append('userNick', userNick);
    fd.append('encodedPassword', encodedPassword);
    
    return this.makePostRequest('actualizarToken', fd).then((res: LoginResponse | any)  => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws actualizarToken');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public async nukaMsjPeticion(nukaMsjPeticion: NukaMsj, contenidoFiles: any): Promise<NukaMsj[]> {
    const fd = new FormData();
    if (nukaMsjPeticion.audioFile) {
      fd.append('audioFile', nukaMsjPeticion.audioFile);
    }
    if (contenidoFiles) {
      for (let i in contenidoFiles) {
        const file = contenidoFiles[Number(i)]
        if (file.blob) {
          const rawFile = new File([file.blob], file.name, {
            type: file.mimeType,
          });
          fd.append('contenidoFile'+i, rawFile, file.name);
        }
      }
    }

    const jsonString = JSON.stringify({
      id: nukaMsjPeticion.id,
      fecha: nukaMsjPeticion.fecha,
      texto: nukaMsjPeticion.texto,
      tipoMsj: nukaMsjPeticion.tipoMsj,
      usuarioId: nukaMsjPeticion.usuarioId,
      chatId: nukaMsjPeticion.chatId,
      needAudio: nukaMsjPeticion.needAudio,
      audioResource: nukaMsjPeticion.audioResource
    });
    fd.append('nukaMsjPeticionJson', jsonString);

    return this.makePostRequest('nukaMsjPeticion', fd, 300000).then(async (res: NukaMsj[]) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws nukaMsjPeticion');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  base64ToArrayBuffer(base64: string) {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  public getResource(id: string, tipo: string, token: string): Promise<Blob | null> {
    /*
    return fetch(this.path + 'getResource?'+ new URLSearchParams({
        id: id,
        tipo: tipo,
      }))
      .then(response => {
        if (!response.ok) {
          return Promise.resolve(null);
        }
        return Promise.resolve(response.blob());
      })
      .catch(error => {
        console.log('error al solicitar resource:', error);
        return Promise.resolve(null);
      });
      */
     return this.getFetchResource(id, tipo, token);
  }

  
  public async mostrarResultadosBets(): Promise<any> {
    const fd = new FormData();

    /*
    const jsonString = JSON.stringify({
      id: modificarCitaOTarea.id,
      fecha: modificarCitaOTarea.fecha,
      texto: modificarCitaOTarea.texto,
      usuarioId: modificarCitaOTarea.usuarioId,
      tipoMsjEstado: modificarCitaOTarea.tipoMsjEstado,
      estado: modificarCitaOTarea.estado,
      imgMsjPath: modificarCitaOTarea.imgMsjPath,
      redireccion: modificarCitaOTarea.redireccion,
      tiempoRepeticion: modificarCitaOTarea.tiempoRepeticion
    });
    */
    //fd.append('modificarCitaOTareaJson', jsonString);

    return this.makePostRequest('mostrarResultadosBets', fd, 300000).then(async (res: any) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws modificarCitaOTarea');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public async modificarCitaOTarea(modificarCitaOTarea: EstadoMsj): Promise<EstadoMsj> {
    const fd = new FormData();

    const jsonString = JSON.stringify({
      id: modificarCitaOTarea.id,
      fecha: modificarCitaOTarea.fecha,
      texto: modificarCitaOTarea.texto,
      usuarioId: modificarCitaOTarea.usuarioId,
      tipoMsjEstado: modificarCitaOTarea.tipoMsjEstado,
      estado: modificarCitaOTarea.estado,
      imgMsjPath: modificarCitaOTarea.imgMsjPath,
      redireccion: modificarCitaOTarea.redireccion,
      tiempoRepeticion: modificarCitaOTarea.tiempoRepeticion
    });
    fd.append('modificarCitaOTareaJson', jsonString);

    return this.makePostRequest('modificarCitaOTarea', fd, 300000).then(async (res: EstadoMsj) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws modificarCitaOTarea');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

  public agregarChat(summary: string, usuarioId: string): Promise<NukaChat> {
    let params: any =  []; 
    params['summary'] = summary;
    params['usuarioId'] = usuarioId
    return this.makeGetRequest('agregarChat', params).then((res: NukaChat) => {
      if (res) {
        return Promise.resolve(res);
      } else {
        return Promise.reject('Error ws agregarChat');
      }
    }).catch((error) => {
      return Promise.reject(error);
    });
  }

}
