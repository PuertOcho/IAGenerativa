import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { DataManagementService } from '../services/data-management.service';

@Injectable({
  providedIn: 'root'
})
export class AuthLoginGuard implements CanActivate {

  constructor(
    private dataManagementService: DataManagementService
  ) {}

  async canActivate(): Promise<boolean> {
    const usuario = await this.dataManagementService.getUsuario();
    if (!usuario) {
      return false;
    }
    return true;
  }
  
}
