import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuController, NavController } from '@ionic/angular';
import { User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { PersistenceService } from 'src/app/services/persistence.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  usuario: User | undefined;
  userNick = 'puertocho';
  password = 'Antonio13';
  esperandoRespuesta: boolean = false;

  constructor(
    private dataManagementService: DataManagementService,
    private navController: NavController,
    private eventsService: EventsService,
    public menu: MenuController
    ) { }

  async ngOnInit() {
    const allPersistenceData = await this.dataManagementService.getAllPersistenceData();
    console.log('LoginPage: allPersistenceData: ', allPersistenceData);
  }

  async login() {
    this.esperandoRespuesta = true;
    this.dataManagementService.login(this.userNick, this.password)
      .then(async (loginResponse) => {
        if (loginResponse && loginResponse.usuario && loginResponse.token) {
          this.usuario = loginResponse.usuario;
          this.eventsService.publish('habilitarMenu', loginResponse.usuario != null);
          await this.dataManagementService.setToken(loginResponse.token);
          await this.dataManagementService.setUsuario(loginResponse.usuario);
          this.esperandoRespuesta = false;
          this.navController.navigateRoot('home-tabs');
        }
      })
      .catch((error) => {
        this.esperandoRespuesta = false;
        console.log(error);
        alert('Credenciales inválidas');
      });
  }

}
