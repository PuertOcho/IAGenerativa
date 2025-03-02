  import { Injectable } from '@angular/core';
  import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
  import { Observable, from, throwError } from 'rxjs';
  import { switchMap, catchError, timeout } from 'rxjs/operators';
import { DataManagementService } from '../services/data-management.service';
import { AppDataModel } from '../app.data.model';
import { AlertController, NavController, Platform } from '@ionic/angular';
import { UtilsService } from '../services/utils.service';
import { Router } from '@angular/router';
import { FingerprintAIO } from '@ionic-native/fingerprint-aio/ngx'
import { EventsService } from '../services/events.service';

  @Injectable()
  export class TokenInterceptor implements HttpInterceptor {

    enProcesoDeAutentificacion: boolean = false; 

    constructor(
      private dataManagement: DataManagementService, 
      private appDataModel: AppDataModel, 
      private platform: Platform,
      private utils: UtilsService,
      private fingerprintAIO: FingerprintAIO,
      public alertController: AlertController,
      public eventsService: EventsService
    ) 
      {}
  
      private async mostrarAlertaActualizarToken() {
        this.enProcesoDeAutentificacion = true;
        const alertaActualizarToken = await this.utils.mostrarAlertaActualizarToken();

        alertaActualizarToken.onWillDismiss().then( async data => {
          if (data && data.role &&  data.role == 'enviar') {
            if (data && data.data && data.data.values && data.data.values[0]) {
              const usuario = await this.dataManagement.getUsuario();
              if (usuario && usuario.userNick && usuario.password) {
                this.dataManagement.login(usuario.userNick, data.data.values[0])
                  .then(async actualizacionRes => {
                    if (actualizacionRes && actualizacionRes.usuario && actualizacionRes.token) {
                      this.dataManagement.setUsuario(actualizacionRes.usuario);
                      this.dataManagement.setToken(actualizacionRes.token);
                      
                      this.eventsService.publish('recargarAppDespuesDeAutentificar', null);

                    } else {
                      console.log('cerrarSesion 2');
                      this.enProcesoDeAutentificacion = false;
                      await this.dataManagement.cerrarSesion();
                    }
                  }).catch(async error => {
                    console.log('TokenInterceptor:  mostrarAlertaActualizarToken:', error);
                    this.enProcesoDeAutentificacion = false;
                    await this.dataManagement.cerrarSesion();
                  });
              }
            } else {
              await this.utils.mostrarAlertaActualizarToken();
            }
          } else {
            console.log('cerrarSesion 1');
            this.enProcesoDeAutentificacion = false;
            await this.dataManagement.cerrarSesion();
          }
        }).finally( () => {
          this.enProcesoDeAutentificacion = false;
        });
      }
      
      private autentificar() {
        this.enProcesoDeAutentificacion = true;
        this.fingerprintAIO.isAvailable().then((_)=> {
          this.fingerprintAIO.show(
            {
              title: 'Autenticación biométrica',
              cancelButtonTitle: 'Cancelar',
              disableBackup: true
            }).then(async (result: any) => {

            console.log('fingerprintAIO result: ', result)

            if (result == 'biometric_success') {
              const usuario = await this.dataManagement.getUsuario();
              if (usuario && usuario.userNick && usuario.password) {
                this.dataManagement.actualizarToken(usuario.userNick, usuario.password)
                  .then(async actualizacionRes => {
                    this.enProcesoDeAutentificacion = false;
                    if (actualizacionRes && actualizacionRes.usuario && actualizacionRes.token) {
                      this.dataManagement.setUsuario(actualizacionRes.usuario);
                      this.dataManagement.setToken(actualizacionRes.token);

                      this.eventsService.publish('recargarAppDespuesDeAutentificar', null);
                    } else {
                      console.log('cerrarSesion 4');
                      await this.dataManagement.cerrarSesion();
                    }
                  }).catch(async error => {
                    console.log(error);
                    this.autentificar()
                  });
              } else {
                console.log('cerrarSesion 5');
                await this.dataManagement.cerrarSesion();
              }
            } else {
              this.autentificar();
            }

          }).catch(async (error: any) => { 
            console.log('fingerprintAIO error: ', error)
            // Si se daq alguno de los siguientes errores, salta de nuevo la autentificacion
            const erroresParaIntentarloDeNuevo = ['BIOMETRIC_DISMISSED']
            if (error && error.message && erroresParaIntentarloDeNuevo.includes(error.message)) {
              this.autentificar();
            } else {
              console.log('cerrarSesion 3');
              this.enProcesoDeAutentificacion = false;
              await this.dataManagement.cerrarSesion();
            }
            console.log(error)
          });

        }, async (err) => {
          console.log('autentificar 2', err);
          this.mostrarAlertaActualizarToken();
        })
      }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> | any {
      return from(this.dataManagement.getToken()).pipe(
        switchMap(token => {
          const modifiedReq = this.addHeaders(req, token);
          return next.handle(modifiedReq);
        }),
        timeout(3600000),
        catchError(async error => {
          if (error && (error.status == '403' || error.status == '401') && this.utils.getCurrentPage().indexOf('login') == -1 && !this.enProcesoDeAutentificacion) {
            const botones = [
              { text: "Autentificar", role: "autentificar" },
              { text: "Salir", role: "salir" }];
              const alertaVerificacion = this.utils.mostrarAlerta("Necesita verificación", "Por favor verifique su identidad", botones, true);

              (await alertaVerificacion).onWillDismiss().then( async (data) => {
              console.log('data', data);
              if (data && data.role &&  data.role == 'autentificar') {
                this.autentificar();
              } else {
                console.log('cerrarSesion 6');
                await this.dataManagement.cerrarSesion();
              }
            });
          }

          if (error && error.url && error.url.indexOf('/nukaMsjPeticion') > 0) {
            await this.eventsService.publish('esperandoRespuestaMensaje', false);
          }
          
          console.log(error);
        })
      );
    }
  
    private addHeaders(req: HttpRequest<any>, token: string): HttpRequest<any> {
      let headers = req.headers;
      if (token) {
        headers = headers.set(this.appDataModel.HEADER_AUTH, this.appDataModel.HEADER_VALUE_BEARER + token);
      }
      const platform = this.platform.is('cordova') ? (this.platform.is('ios') ? 'ios' : 'android') : 'web';
      headers = headers.set('x-platform', platform);
  
      return req.clone({ headers });
    }

  }
  
