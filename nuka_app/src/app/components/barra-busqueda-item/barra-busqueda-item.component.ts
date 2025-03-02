import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { NavigationExtras } from '@angular/router';
import { NavController } from '@ionic/angular';
import { NukaChat, NukaMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-barra-busqueda-item',
  templateUrl: './barra-busqueda-item.component.html',
  styleUrls: ['./barra-busqueda-item.component.scss'],
})
export class BarraBusquedaItemComponent implements OnChanges {

  @Input() busquedaMsj: any[] = []
  @Input() chatsList: any[] = []
  @Output() textoBusquedaEvent: EventEmitter<any> = new EventEmitter<any>();
    
  
  busquedaMsjChats: any[] = []
  textoBusqueda: string = '';
  idChatMapNombres = new Map;
  loading: boolean = true;
  chatsListIds: string[] = []

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public navController: NavController
  ) { }

  async ngOnChanges(changes: SimpleChanges): Promise<void> {
    this.loading = true;
    const busquedaMsj: string[] = this.busquedaMsj.map(m => m.chatId)
    this.busquedaMsjChats = await this.dataManagementService.getNukaChatsByIds(busquedaMsj);
    this.chatsListIds = this.chatsList.map(m => m.id);
    this.idChatMapNombres = new Map();
    this.busquedaMsjChats.forEach(bm => {
      this.idChatMapNombres.set(bm.id, bm.summary);
    })
    this.loading = false;
  }
  
  public getNombreChat(m: NukaMsj): string {
    return String(this.idChatMapNombres.get(m.chatId))
  }

  public redirect(m: NukaMsj) {
    if (m && m.chatId && this.chatsListIds.includes(m.chatId)) {
      this.navController.navigateForward('details/' + m.chatId, { queryParams: { redirectMsjId: m.id } });
    } 
  }

  ngOnInit() {
    this.loading = false;
    this.chatsListIds = this.chatsList.map(c => c.id)
  }

  handleInputBuscador(event: any) {
    if (event?.target?.value && this.chatsListIds) {
      this.textoBusqueda = event?.target?.value;
    } else {
      this.textoBusqueda = '';
    }
    this.textoBusquedaEvent.emit(this.textoBusqueda);
  }

  inputTextoBusqueda() {
    this.textoBusquedaEvent.emit(this.textoBusqueda);
  }

}
