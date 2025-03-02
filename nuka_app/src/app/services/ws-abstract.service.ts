import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { ConfigService } from '../config/config.service';
import { EventsService } from './events.service';
import { UtilsService } from './utils.service';
import { timeout } from 'rxjs/operators';
import { PersistenceService } from './persistence.service';
import { Network } from '@capacitor/network';

@Injectable({
  providedIn: 'root',
})
export class WsAbstractService {
  path = '';
  pathNoAuth = '';
  url = '';
  restIsWorking: any;
  metodosSinAutentificacion = ['login', 'test', 'actualizarToken'];

  tiempoMilesugndosReintento: number = 7000;

  constructor(
    public http: HttpClient,
    public config: ConfigService,
    public util: UtilsService,
    public events: EventsService,
    public persistence: PersistenceService
  ) {
    this.path = this.config.config().restUrlPrefix;
    this.pathNoAuth = this.config.config().restUrlNoAuthPrefix;
    this.url = this.config.config().restUrl;

    this.events.subscribe('cambiadoEnviroment', (_) => {
      this.path = this.config.config().restUrlPrefix;
      this.pathNoAuth = this.config.config().restUrlNoAuthPrefix;
      this.url = this.config.config().restUrl;
    });
  }

  private async checkNetworkStatus() {
    const status = await Network.getStatus();
    return status.connected;
  }

  protected makeGetRequest(
    path: string,
    paramsRequest: any,
    timeoutParam?: number
  ): Promise<any> {
    paramsRequest = !paramsRequest ? {} : paramsRequest;
    return new Promise(async (resolve, reject) => {
      if (await this.checkNetworkStatus()) {
        const timeoutValue = timeoutParam ? timeoutParam : 2400000; // 40 mint
        this.restIsWorking = this.http
          .get(this.getRoute(path), { params: paramsRequest })
          .pipe(timeout(timeoutValue), )
          .subscribe(
            (response) => {
              resolve(response);
            },
            (err) => {
              if (err.status === 200) {
                resolve(err.error?.text);
              } else {
                reject(err);
              }
            }
          );
      } else {
        this.events.publish('sin-conexion', true);
        try {
          await this.cancelRest();
        } finally {
          setTimeout(() => {
            resolve(this.makeGetRequest(path, paramsRequest, timeoutParam));
          }, this.tiempoMilesugndosReintento);
        }
      }
    });
  }

  protected makePostRequest(
    path: string,
    body: any,
    timeoutParam?: number
  ): Promise<any> {
    return new Promise(async (resolve, reject) => {
      if (await this.checkNetworkStatus()) {
        this.events.publish('sin-conexion', false);
        const timeoutValue = timeoutParam ? timeoutParam : 2400000; // 40 mint 300 x 1000
        this.http
          .post(this.getRoute(path), body)
          .pipe(timeout(timeoutValue))
          .subscribe(
            (response) => {
              resolve(response);
            },
            (error) => {
              reject(error);
            }
          );
      } else {
        this.events.publish('sin-conexion', true);

        try {
          await this.cancelRest();
        } finally {
          setTimeout(() => {
            resolve(this.makePostRequest(path, body, timeoutParam));
          }, this.tiempoMilesugndosReintento);
        }
      }
    });
  }

  public getFetchResource(
    id: string,
    tipo: string,
    token: string
  ): Promise<Blob | null> {
    return fetch(
      this.path + 'getResource?' + new URLSearchParams({ id, tipo }),
      {
        headers: new Headers({
          Authorization: `Bearer ${token}`,
        }),
      }
    )
      .then((response) => {
        if (!response.ok) {
          return Promise.resolve(null);
        }
        return Promise.resolve(response.blob());
      })
      .catch((error) => {
        console.log('error al solicitar resource:', error);
        return Promise.resolve(null);
      });
  }

  protected cancelRest(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.restIsWorking.unsubscribe(
        (response: any) => {
          resolve(response);
        },
        (error: any) => {
          console.log(error);
          reject(error);
        }
      );
    });
  }

  protected makeGetImageRequest(path: string): Promise<string> {
    return new Promise((resolve, reject) => {
      const headers: HttpHeaders = this.getHeadersImage();
      this.http
        .get(this.getRoute(path), { headers, responseType: 'blob' })
        .subscribe(
          (response: Blob) => {
            const reader = new FileReader();

            reader.onload = () => {
              if (typeof reader.result === 'string') {
                return resolve(reader.result);
              } else {
                return reject('Unexpect file type');
              }
            };
            reader.onerror = () => reject('File error!');
            reader.readAsDataURL(response);
          },
          (err) => {
            reject('Error');
          }
        );
    });
  }

  protected makeDeleteRequest(
    path: string,
    paramsRequest: any,
    checkInactivity: boolean
  ): Promise<any> {
    paramsRequest = !paramsRequest ? {} : paramsRequest;
    if (checkInactivity) {
      this.events.publish('checkInactivity');
    }
    return new Promise((resolve, reject) => {
      this.http
        .delete(this.getRoute(path), { params: paramsRequest })
        .subscribe(
          (response) => {
            resolve(response);
          },
          (err) => {
            if (err.status === 200) {
              resolve(null);
            } else {
              console.log(err);
              reject(err);
            }
          }
        );
    });
  }

  private getRoute(name: string): string {
    if (this.metodosSinAutentificacion.includes(name)) {
      return this.pathNoAuth + name;
    } else {
      return this.path + name;
    }
  }

  private getHeadersImage(): HttpHeaders {
    let headers = new HttpHeaders();
    headers = new HttpHeaders().set('Accept', 'image/png');
    return headers;
  }
}
