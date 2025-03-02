import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { DataManagementService } from '../services/data-management.service';
import { NavController } from '@ionic/angular';
import { EventsService } from '../services/events.service';

@Injectable({
  providedIn: 'root'
})
export class AuthMenuGuard implements CanActivate {

  constructor(
    private dataManagementService: DataManagementService,
    private eventsService: EventsService,
  ) {}

  async canActivate(): Promise<boolean> {
      const usuario = await this.dataManagementService.getUsuario();
      if (usuario) {
        await this.eventsService.publish('habilitarMenu', true);
      } else {
        await this.eventsService.publish('habilitarMenu', false);
      }
    return true;
  }
  
}
