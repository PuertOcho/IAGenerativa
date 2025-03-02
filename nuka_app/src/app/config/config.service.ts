import { Injectable } from '@angular/core';
import { EventsService } from '../services/events.service';
import { PersistenceService } from '../services/persistence.service';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  //finalUrlPrefix = 'https://dev.puertocho.com/';
  // finalUrlPrefix = 'https://puertocho.com/';
  finalUrlPrefix = 'http://localhost:9898/';
  // finalUrlPrefix = 'https://dark-largely-layers-director.trycloudflare.com/';
  
  platform = 'MOVIL';
  enviroment = 'dev';
  finalWSUrlPrefix = 'ws://dev.puertocho.com/';

  constructor(
    public events: EventsService,
    public persistance: PersistenceService,
  ) { }

  async cambiarUrlPrefix(env: string, url?: string) {
    if (env == "dev") {
      this.finalUrlPrefix = 'https://dev.puertocho.com/';
      this.enviroment = env;
      this.events.publish('cambiadoEnviroment');

    } else if (env == "master") {
      this.finalUrlPrefix = 'https://puertocho.com/';
      this.enviroment = env;
      this.events.publish('cambiadoEnviroment');
      
    } else if (env == "custom" && url) {
      this.finalUrlPrefix = url;
      this.enviroment = env;
      this.events.publish('cambiadoEnviroment');
    } else {
      console.error("Se ha intentado cambiarUrlPrefix() pero no se dio parametro valido:", env);
    }
  }

  public config() {
    const enviroment = this.enviroment;
    const urlPrefix = this.finalUrlPrefix;
    const wsUrl = this.finalWSUrlPrefix;
    const urlAPI = 'api/';
    const noAuthUrlAPI = 'noAuth/';
    const appVersion = '0.4-24.01.15';

    const webMobile = false;
    const schema = 'nuka';
    const platform = this.platform;
    const numMsjPorRecarga = 25;  // Numero de mensajes por pagina en Chat 
    const itemPorPagina = 10;  // Numero de compras por pagina en Historial de compras
    const habilitarSplashScreen = false;
    
    return {
      restUrlPrefix: urlPrefix + urlAPI,
      restUrlNoAuthPrefix: urlPrefix + noAuthUrlAPI,
      restUrl: urlPrefix,
      wsUrl: wsUrl,
      version: appVersion,
      webMobile: webMobile,
      schema: schema,
      platform: platform,
      numMsjPorRecarga: numMsjPorRecarga,
      itemPorPagina: itemPorPagina,
      habilitarSplashScreen: habilitarSplashScreen,
      enviroment: enviroment
    };
  }
}