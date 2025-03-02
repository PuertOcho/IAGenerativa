import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { DataManagementService } from '../services/data-management.service';
import { NavController } from '@ionic/angular';
import { UtilsService } from '../services/utils.service';

@Injectable({
  providedIn: 'root'
})
export class AuthChatsGuard implements CanActivate {

  constructor(
    private dataManagementService: DataManagementService,
    private utils: UtilsService,
    public navController: NavController
  ) {}
  
  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Promise<boolean> {
      const user = await this.dataManagementService.getUsuario();
      if (user.opcionesModal) {
        const redirigirOpcionBasica = this.utils.getOpcionBasicaById(user.opcionesModal, 'CHATS_REDIRIGIR_CHAT_GENERAL');
        if (redirigirOpcionBasica && redirigirOpcionBasica.valor && redirigirOpcionBasica.valor.length > 0 &&
          redirigirOpcionBasica.valor[0] === 'true') {
          const infoChatGeneral = await this.dataManagementService.getInfoChatGeneral();
          if (infoChatGeneral && infoChatGeneral.id) {
            this.navController.navigateForward('details/' + infoChatGeneral.id);
          }
        }
      }
      return true;    
  }
  
}
