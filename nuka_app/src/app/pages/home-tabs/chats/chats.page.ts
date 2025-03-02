import { AfterViewChecked, Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { PopoverController } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { Filtro, GrupoOpciones, NukaChat, NukaMsj, OpcionBasica, User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-chats',
  templateUrl: './chats.page.html',
  styleUrls: ['./chats.page.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ChatsPage implements OnInit {
  chatsList: any[] = [];
  user: User | any = null;
  loading: boolean = true;
  chatsListKeys: string[] = [];
  chatsListByTime = new Map;
  chatsListFavoritos: any[] = [];
  textoBusqueda: string = '';
  busquedaMsj: NukaMsj[] = [];
  mostrarFavoritos: boolean = true;
  tagsDisponibles: string[] = [];
  tagsEnFiltro: string[] = [];

  filtroIncluirTagActivo: boolean = true;
  //filtroExcluirTagActivo: boolean = false;
  //filtroFechaActivo: boolean = false;
  popoverFiltroIncluirTag: boolean = false;
  
  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public router: Router,
    public eventsService: EventsService,
    public appData: AppDataModel,
    public popoverController: PopoverController
  ) { 
    this.eventsService.subscribe('actualizarChat', async c => {
      const indice = this.chatsList.findIndex(chat => chat.id === c.id);
      if (indice !== -1) {
        this.chatsList[indice] = c;
        await this.actualizarChatsList();
      } else {
        console.error("El objeto con el id especificado no se encontró en la lista.");
      }
    });

  }

  public async eliminartagsEnFiltro() {
    this.tagsEnFiltro= [];
    await this.dataInit();
  }

  public ionChangeIonSelect(event: any) {
    console.log('change event', event);
  }

  public async ionDismissIonSelect(event: any) {
    if (event && event?.target && event?.target?.value && event?.target.value != null) {
      this.tagsEnFiltro = event?.target.value;
      if (this.tagsEnFiltro && this.tagsEnFiltro.length > 0) {
        const filtro: Filtro  = new Filtro('GENERAL_TAGS', [], this.tagsEnFiltro);
        await this.dataInit(undefined, [filtro]);
      }
    }
  }

  public getTagsPorChat(chat: NukaChat): string[] {
    if (chat && chat.tags != null) {
      return chat.tags;
    }    
    return [];
  }

  public checkIsFilterTagChecked(tag: string) {
    return this.tagsEnFiltro.includes(tag);
  }

  public checkboxFiltroIncluirTagCambiado(event: any) {
    if (event && event?.detail && event?.detail?.value) {
      const value = event?.detail?.value;;
      const checked = event?.detail?.checked;
      if (checked == true && !this.tagsEnFiltro.includes(value)) {
        this.tagsEnFiltro.push(value);
      } else if (checked == false && this.tagsEnFiltro.includes(value)) {
        this.tagsEnFiltro = this.tagsEnFiltro.filter(item => item !== value);
      }
    }
  }

  public toggleFiltroActivo(nombreFiltro: string) {
    if (nombreFiltro == 'incluir') {
      this.filtroIncluirTagActivo = !this.filtroIncluirTagActivo;
    }
  }

  public asignarColorFiltroSeleccionado(nombreFiltro: string) {
    let estaActivo = false;
    if (nombreFiltro == 'incluir') {
      estaActivo = this.filtroIncluirTagActivo;
    }
    return estaActivo ? 'primary': 'medium';
  }

  public async toggleMostrarFavoritos() {
    this.mostrarFavoritos = !this.mostrarFavoritos;

    let filtro: Filtro | undefined = undefined;
    if (this.filtroIncluirTagActivo && this.tagsEnFiltro && this.tagsEnFiltro.length > 0) {
      filtro  = new Filtro('GENERAL_TAGS', [], this.tagsEnFiltro);
    }
    await this.dataInit(undefined, filtro ? [filtro]: undefined);
  }

  chatDraged(event: any, slidingItem: any) {
    //console.log('event', event)
    if (event.detail.ratio === 1) {
      //slidingItem.closeOpened();
    }
  }
  
  public async eliminarNukaChat(chat: NukaChat, slidingItem: any) {
    console.log('chat', chat)
    slidingItem.closeOpened();
    if (chat && chat.id) {
      const bool = await this.dataManagementService.eliminarNukaChat(chat.id);
      if (bool) {
        await this.dataInit(this.chatsList.filter((c) => c.id !== chat.id));
        return true;
      }
    }
    return false;
  }

  public getSizeBarraBusqueda(): number {
    if (this.textoBusqueda && this.textoBusqueda != '') {
      return 12;
    }
    return 10
  }

  public async handleInputBuscador(textoBusqueda: string) {
    this.textoBusqueda = textoBusqueda;

    if (textoBusqueda) {
      this.busquedaMsj = await this.dataManagementService.getBusquedaChat(this.textoBusqueda, this.user.id);
    } else {
      this.busquedaMsj = [];
    }
  }

  public getMesYAnno(chat: NukaChat): string | null {
    if (chat && chat.fechaUltimoMsj) {
      const timestamp = parseInt(chat.fechaUltimoMsj, 10);
      const fecha = new Date(timestamp);
      let fechaString = this.utils.obtenerNombreMes(fecha.getMonth()) + ' de ' + fecha.getFullYear().toString()
      if (!this.chatsListKeys.includes(fechaString)) {
        this.chatsListKeys.push(fechaString);
      }
      return fechaString;
    }
    return null;
  }

  public mostrarItemDivider(chat: NukaChat) {
    ////console.log('mostrarItemDivider', chat)
    let fechaString: any; 
    if (chat && chat.fechaUltimoMsj) {
      const timestamp = parseInt(chat.fechaUltimoMsj, 10);
      const fecha = new Date(timestamp);
      fechaString = this.utils.obtenerNombreMes(fecha.getMonth()) + ' de ' + fecha.getFullYear().toString();
    }
    return !this.chatsListKeys.includes(fechaString);
  }

  public async toogleFavoriteStatus(chat: NukaChat | any) {
    if (await this.dataManagementService.toogleFavoriteStatus(chat.id)) {
      chat.favorito = !chat.favorito; 
    }
    
    let filtro: Filtro | undefined= undefined;
    if (this.filtroIncluirTagActivo && this.tagsEnFiltro && this.tagsEnFiltro.length > 0) {
      filtro  = new Filtro('GENERAL_TAGS', [], this.tagsEnFiltro);
    }
    await this.dataInit(undefined, filtro ? [filtro]: undefined);
  }

  public getChatImages(chat: NukaChat) {
    return this.utils.getImagenChat(chat);
  }

  public getChatsListByTimeOnlyNoFavorites(): Map<any, any> {
    this.chatsListByTime = this.chatsList.filter(c => !c.favorito).sort((c1:any, c2:any) => {
      if (c1.fechaUltimoMsj > c2.fechaUltimoMsj) {
        return -1;
      }
      if (c1.fechaUltimoMsj < c2.fechaUltimoMsj) {
        return 1;
      }
      return 0;
    }).reduce((map, chat) => {
        let fecha = this.getMesYAnno(chat);
        if (fecha) {
          fecha = fecha.charAt(0).toUpperCase() + fecha.slice(1)
        }

        if (map.has(fecha)) {
          map.get(fecha).messages.push(chat);
        } else {
          map.set(fecha, {messages: [chat] });
        }
        return map;
    }, new Map<string, { fecha: string, messages: any[] }>());
    return this.chatsListByTime;
  }

  public getChatsListByTime() {
    return this.chatsList.sort((c1:any, c2:any) => {
      if (c1.fechaUltimoMsj > c2.fechaUltimoMsj) {
        return -1;
      }
      if (c1.fechaUltimoMsj < c2.fechaUltimoMsj) {
        return 1;
      }
      return 0;
    }).reduce((map, chat) => {
        let fecha = this.getMesYAnno(chat);
        if (fecha) {
          fecha = fecha.charAt(0).toUpperCase() + fecha.slice(1)
        }

        if (map.has(fecha)) {
          map.get(fecha).messages.push(chat);
        } else {
          map.set(fecha, {messages: [chat] });
        }
        return map;
    }, new Map<string, { fecha: string, messages: any[] }>());
  }

  public getTagsDisponibles(): string[] {
    const chatGeneral = this.chatsList.find(c => c.summary == 'GENERAL');
    if (chatGeneral && chatGeneral.opcionesModal) {
      const ob = this.utils.getOpcionBasica(chatGeneral.opcionesModal, 'GENERAL', 'GENERAL_TAGS');
      return this.utils.getDatosOpcion(ob);
    }
    return [];
  }

  async dataInit(chatsList?: any[], filtros?: Filtro[]) {
    this.loading = true;
    this.user = await this.dataManagementService.getUsuario();
    if (chatsList) {
      this.chatsList = chatsList;
    } else {
      try {
        this.chatsList = await this.dataManagementService.getNukaChats(this.user.id, filtros).finally(() => {});
      } catch (error) {
        console.error('Error dataInit chats:', error);
      }
    }
    await this.dataManagementService.setInfoChatGeneral(this.chatsList.find(c => c.summary === 'GENERAL'));
    
    const tagsUnicos: Set<string> = new Set();
    this.chatsList.forEach((chat: NukaChat) => {
      if (chat && chat.tags) {
        chat.tags.forEach((tag) => {
          tagsUnicos.add(tag);
        });
      }
    });
    this.tagsDisponibles = Array.from(tagsUnicos);

    if (this.chatsList) {
      if (this.mostrarFavoritos) {
        this.chatsListFavoritos = this.chatsList.filter(c => c.favorito).sort((c1, c2) => {
          if (c1.fechaUltimoMsj > c2.fechaUltimoMsj) {
            return -1;
          }
          if (c1.fechaUltimoMsj < c2.fechaUltimoMsj) {
            return 1;
          }
          return 0;
        });
        this.chatsListByTime = await this.getChatsListByTimeOnlyNoFavorites();

      } else {
        this.chatsListFavoritos = [];
        this.chatsListByTime = await this.getChatsListByTime();
      }

      const fechaAux = [...this.chatsListByTime.keys()];
      const fechasOrdenadas = fechaAux.sort((a, b) => {
        const fechaA = this.parseFecha(a);
        const fechaB = this.parseFecha(b);
        return fechaA.getTime() - fechaB.getTime();
      });
      this.chatsListKeys = fechasOrdenadas.reverse();
    }
    this.loading = false;
  }

  ngOnInit() {
    this.dataInit();
  }

  public redirigirAChatdetails(chatID: any) {
    let ruta: string = 'details/' + chatID;
    this.router.navigate([ruta]);
  }

  public async agregarChat () {
    const user = this.user ? this.user: await this.dataManagementService.getUsuario();
    if (user && user.id) {
      const nukaChat = await this.dataManagementService.agregarChat('Chat sin titulo', user.id);
      this.chatsList = await this.dataManagementService.getNukaChats(user);
      await this.actualizarChatsList();
      this.redirigirAChatdetails(nukaChat.id)
    } else {
      this.utils.presentToast('No se pudo agregar Chat', undefined, 'danger')
    }
  }

  public async actualizarChatsList() {
    let filtro: Filtro | undefined = undefined;
    if (this.filtroIncluirTagActivo && this.tagsEnFiltro && this.tagsEnFiltro.length > 0) {
      filtro  = new Filtro('GENERAL_TAGS', [], this.tagsEnFiltro);
    }
    await this.dataInit(undefined, filtro ? [filtro]: undefined);
  }

  public parseFecha(fechaStr: string): Date {
    const parts = fechaStr.split(" de ");
    if (parts.length === 2) {
      const mes = this.utils.obtenerNumeroMes(parts[0]);
      const año = parseInt(parts[1]);
      if (año && mes) {
        const fecha = new Date();
        fecha.setDate(1);
        fecha.setMonth(mes);
        fecha.setFullYear(año);
        if (!isNaN(fecha.getTime())) {
          return fecha;
        }
      }
    }

    console.error("Formato de fecha no válido: " + fechaStr);
    return null as any;
  }

  public async handleRefresh(event: any) {
    let filtro: Filtro | undefined= undefined;
    if (this.filtroIncluirTagActivo && this.tagsEnFiltro && this.tagsEnFiltro.length > 0) {
      filtro  = new Filtro('GENERAL_TAGS', [], this.tagsEnFiltro);
    }
    await this.dataInit(undefined, filtro ? [filtro]: undefined);
    event?.target?.complete();
  }

}
