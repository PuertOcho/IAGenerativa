import { Component, OnInit, ViewChild } from '@angular/core';
import { IonTabs } from '@ionic/angular';
import { User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-home-tabs',
  templateUrl: 'home-tabs.page.html',
  styleUrls: ['home-tabs.page.scss']
})
export class HomeTabsPage implements OnInit {

  @ViewChild('tabsDOM', { static: false }) tabsDOM: IonTabs | any;
  public user: User | undefined;
  selectedTab: string = 'status';

  tabs: any[] = [
    { id: 'status', permiso: 'ANY', icon: 'assets/icon/robot5', extension: '.png', duracion: 2400 },
    { id: 'chats', permiso: 'ANY', icon: 'assets/icon/diagram3', extension: '.png', duracion: 1670 },
    { id: 'settings', permiso: 'ANY', icon: 'assets/icon/settings2', extension: '.png', duracion: 2480 }
  ];

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService
  ) {}

  public click(tab: any) {
    tab.extension = '.gif'
    setTimeout(() => {
      tab.extension = '.png'
    }, tab.duracion);
  }


  public ionTabsDidChange(event: any) {
    this.selectedTab =  event && event.tab ? event.tab: 'status';
  }

  public asignarClaseIcono(tab: any) {
    // return  this.selectedTab == tab.id ? 'animated-icon': 'null';
    return  this.selectedTab == tab.id ? 'animated-icon': 'no-animated-icon';
  }

  public asignarTamanoIconoTab(tab: any) {
    return tab && tab.id == 'status' ? { zoom: 1.1 }: { zoom: 1 };
  }

  public getIcon(tab: any): string {
    return tab.icon + tab.extension;
  }

  async ngOnInit () {
    this.user = await this.dataManagementService.getUsuario();
  }

  async ionViewDidEnter() {
    this.user = await this.dataManagementService.getUsuario();
    //console.log('home-tabs this.user', this.user)
  }

}
