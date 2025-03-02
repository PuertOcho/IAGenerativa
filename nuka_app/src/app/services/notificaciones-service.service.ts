import { Injectable } from '@angular/core';
import { LocalNotifications, LocalNotificationsPlugin, ScheduleOptions } from '@capacitor/local-notifications'
import { PushNotifications, PushNotificationsPlugin } from '@capacitor/push-notifications';
import { BackgroundRunner } from '@capacitor/background-runner';
import {Capacitor} from '@capacitor/core';
import { UtilsService } from './utils.service';
import { DataManagementService } from './data-management.service';

@Injectable({
  providedIn: 'root'
})
export class NotificacionesService {

  id: number = 1;

  constructor(
    public utils: UtilsService,
    public dataManagementService: DataManagementService,
  ) { 

    PushNotifications.addListener('registration', async (token) => {
      const seHaActualizado = await this.dataManagementService.actualizarTokenDispositivo(String(token.value));
      if (seHaActualizado) {
        this.utils.debug('info', 'NotificacionesService', 'constructor', 'Se ha actualizado el token del dispositivo token->' + String(token.value));
      }
    });

    PushNotifications.addListener('registrationError', (err)=> {
        console.log('registrationError', err);
    }); 
    
    PushNotifications.addListener('pushNotificationReceived', (notifications) => {
        console.log('pushNotificationReceived',notifications);
    });

    PushNotifications.addListener('pushNotificationActionPerformed', notification => {
      console.log('Push action performed: ', notification);
      this.handleNotificationClick(notification);
    });

  }

  initPush() {
    if (Capacitor.getPlatform() !== 'web') {
        this.registerPush();
    }
  }

  private handleNotificationClick(notification: any) {
    // Extract the notification payload as needed
    const data = notification.notification.data;
  
    // Perform an action based on the click_action or other data
    if (data.click_action === 'OPEN_HOME') {
      // Navigate to the home page or perform other logic
    }
  }
  

  private registerPush() {
    PushNotifications.requestPermissions().then(permission => {
        if (permission.receive === 'granted') {
            PushNotifications.register();
        }
        else {
            // If permission is not granted
        }
    });

    PushNotifications.addListener('registration', (token) => {
        console.log('registration', token);
    });

    PushNotifications.addListener('registrationError', (err)=> {
        console.log('registrationError', err);
    }); 
    
    PushNotifications.addListener('pushNotificationReceived', (notifications) => {
        console.log('pushNotificationReceived',notifications);
    });

    PushNotifications.addListener('pushNotificationActionPerformed', notification => {
      console.log('Push action performed: ', notification);
      this.handleNotificationClick(notification);
    });
  }

}
