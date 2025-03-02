import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController, IonDatetime, IonModal, ModalController, NavController } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { EstadoMsj, GrupoOpciones, OpcionBasica, OpcionesModal, User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { EventsService } from 'src/app/services/events.service';
import { UtilsService } from 'src/app/services/utils.service';
import { BackgroundRunner } from '@capacitor/background-runner';
import { NotificacionesService } from 'src/app/services/notificaciones-service.service';
import { random } from 'faker';

@Component({
  selector: 'app-status',
  templateUrl: './status.page.html',
  styleUrls: ['./status.page.scss'],
})
export class StatusPage implements OnInit {
  @ViewChild(IonModal) modal: IonModal | any;
  
  disabledChangePageDatetime: boolean | any;
  mesYAnyoActual: string | any;
  loading: boolean = true;
  usuario: User | undefined;
  email: string = '';
  password: string = '';
  segmento: string = 'NOTIFICACIONES';
  segmentoDispositivo: any;   
  segmentos: any[] = [
    { name: 'Alertas',  icon: 'notifications', id: 'NOTIFICACIONES', badge: { value: null, color: 'danger' }},
    { name: 'Calendario',  icon: 'calendar', id: 'CALENDARIO', badge: { value: null, color: 'danger' }},
    { name: 'Tareas',  icon: 'list', id: 'TAREAS', badge: { value: null, color: 'danger' }},
    //{ name: 'Dispositivos',  icon: 'desktop', id: 'DISPOSITIVOS'}
  ];
  notificaciones: any[] = [];
  eventosYCitas: any[] = [];
  eventosYCitasToday: any[] = [];
  eventosYCitasTomorrow: any[] = [];
  fechasMarcadas: any[] = [];
  diaSeleccionado: any | string;
  tareas: any[] = [];
  tareasPorHacer: any[] = [];
  tareasInfo: any[] = [];
  dispositivos: any[] = [
    { name: 'Device 1',  icon: 'desktop-outline', id: 1},
    { name: 'Device 2', icon: 'laptop-outline', id: 2},
    { name: 'Device 3', icon: 'tv-outline', id: 3},
    { name: 'Device 4', icon: 'phone-portrait-outline', id: 4},
    { name: 'Device 5', icon: 'desktop-outline', id: 5},
    { name: 'Device 6', icon: 'laptop-outline', id: 6},
    { name: 'Device 7', icon: 'tv-outline', id: 7},
    { name: 'Device 8', icon: 'phone-portrait-outline', id: 8},
  ];
  isCitaModalOpen: boolean = false;
  isModificarCitaModalOpen: boolean = false;
  eventoParaModificar: any = new EstadoMsj();

  isTareaModalOpen: boolean = false;
  isCitaColoresModalOpen: boolean = false;
  textoModal: any;
  diasRepeticionModal: any;
  listUsuarioModal: any = ['puertocho', 'alba'];
  usernameModal: any;
  colorEstadoEvento: string = 'var(--ion-color-primary)';
  coloresYEtiquetas: any[] = [
    {color: 'var(--ion-color-primary)', etiqueta: 'General'},
    {color: '#ffcd00', etiqueta: 'Aficiones'},
    {color: '#65db36', etiqueta: 'Ejercicio'},
    {color: '#ca72df', etiqueta: 'Labores'},
    {color: '#a28360', etiqueta: 'Trabajo'},
    {color: '#1dadf7', etiqueta: 'Calendario'}
  ];
  eventosYCitasDiaSeleccionado: any[] = [];
  
  constructor(
    public utils: UtilsService,
    public dataManagementService: DataManagementService,
    private navCtrl: NavController,
    public router: Router,
    private datePipe: DatePipe,
    public appData: AppDataModel,
    public eventsService: EventsService,
    private route: ActivatedRoute,
    public modalCtrl: ModalController,
    public alertCtrl: AlertController,
    public notificacionesService: NotificacionesService,
  ) {
    this.init();

    this.eventsService.subscribe('recargarAppDespuesDeAutentificar', async (_) => {
      // Tenemos que comprobar si tenemos unas paginas de ajustes abierta (Modal) y cerrarla si lo esta. Igual con las alertas
      if (await this.modalCtrl.getTop()) {
        await this.modalCtrl.dismiss();
      }
      if (await this.alertCtrl.getTop()) {
        await this.alertCtrl.dismiss();
      }

      await this.router.navigate(['home-tabs']); 
      this.segmento = 'NOTIFICACIONES';
      this.actualizarNotificaciones();
      this.actualizarEventosYCitas();
      this.actualizarTareas();
    });

    this.eventsService.subscribe('modificarItemCalendario', async (accionModificarCalendario: any) => {
      if (accionModificarCalendario != undefined) {
        this.isModificarCitaModalOpen = accionModificarCalendario.modificar;
        const eventoParaModificar =  this.eventosYCitas.find(e => e.id == accionModificarCalendario.id);
        if (eventoParaModificar) {
          this.eventoParaModificar = eventoParaModificar;
        }
        console.log('accionModificarCalendario', accionModificarCalendario);
        console.log('eventosYCitas', this.eventosYCitas);
        console.log('eventoParaModificar', this.eventoParaModificar);
      }
    });
  }

  comprobarSiEstamosEnEsteMesYAnyo(): boolean {
    let monthText: any = document.querySelector('ion-datetime')?.shadowRoot?.querySelector('.calendar-header')?.querySelector('ion-label');
    if (monthText && monthText.textContent) {
      try {
        const anyo = Number((monthText.textContent as string).split('de')[1].replace(' ', ''));
        const nombreMes = String((monthText.textContent as string).split('de')[0].replace(' ', ''));

        return Boolean(nombreMes && anyo && this.utils.isThisMonth(this.utils.obtenerNumeroMes(nombreMes) , anyo));
      } catch {
        return false
      }
    }
    return false
  }

  modificacionesVisualesEnCalendario() {
    setTimeout(() => {
      const ionDatetime = document.querySelector('ion-datetime');
      if (ionDatetime) {
        const shadowRoot = ionDatetime.shadowRoot;
        if (shadowRoot) {
          const calendarDayToday: any = shadowRoot.querySelector('.calendar-day-today');
          if (calendarDayToday && this.comprobarSiEstamosEnEsteMesYAnyo()) {
            calendarDayToday.style.border = '2px solid var(--ion-color-primary)';
            calendarDayToday.style.marginTop = '22px';
            calendarDayToday.style.marginBottom = '20px';
            calendarDayToday.style.borderRadius = '50%';
          } else {
            const calendarDay: any = shadowRoot.querySelector('.calendar-day');
            console.log('calendarDay', calendarDay);
            calendarDay.style.border = '0px solid transparent';
            calendarDay.style.marginTop = '0px';
            calendarDay.style.marginBottom = '0px';
            calendarDay.style.borderRadius = '0%';
          }  

          // Para TEST
          // const calendarDayActive: any = shadowRoot.querySelector('.calendar-day-active');
          // if (calendarDayActive) { console.log('calendarDayActive', calendarDayActive); }
        }
      }
    }, 100);
  }

  async init() {
    try {
      this.notificacionesService.initPush();
    } catch (err) {
      console.log(`ERROR: ${err}`);
    }
  }

  public setAlturaDeCirculos() {
    const circulo1 = document.getElementById('circulo-1');
    if (circulo1) {
      circulo1.style.top = String((this.utils.getRandomInt(20)*-10) + 'px');
      circulo1.style.animationDuration = '25s';  
    }

    const circulo3 = document.getElementById('circulo-3');
    if (circulo3) {
      circulo3.style.top = String((this.utils.getRandomInt(20)*-10) + 'px');
      circulo3.style.animationDuration = '25s';    
    }

    const circulo10 = document.getElementById('circulo-10');
    if (circulo10) {
      circulo10.style.top = String((this.utils.getRandomInt(20)*-10) + 'px');
      circulo10.style.animationDuration = '25s';       
    }

    setTimeout(() => {
      if (circulo1) { circulo1.style.top = '0'; }
      if (circulo3) { circulo3.style.top = '0'; }
      if (circulo10) { circulo10.style.top = '0'; }
    }, 15000);
  }
    
  async ionViewWillEnter() {
    this.setAlturaDeCirculos();

    this.usuario = await this.dataManagementService.getUsuario();      
    this.eventsService.publish('actualizarUsario', null);
      this.route.queryParams.subscribe(_p => {
        const navParams = this.router.getCurrentNavigation()?.extras.state
        if (navParams as any) {
          let segmento = (navParams as any).segmento;
          if (segmento) {
            this.actualizarNotificaciones();
            this.actualizarEventosYCitas();
            this.actualizarTareas();
            this.segmento = segmento;
          }
        } 
      })
  }

  public async actualizarNotificaciones() {
    if (this.usuario && this.usuario.id) {
      this.notificaciones = await this.dataManagementService.getNotificaciones(this.usuario.id);
      this.notificaciones = this.notificaciones.sort(function(a, b){return a.fecha - b.fecha});
      this.actualizarNumeroNotificaciones();
    }
  }

  public getIconSegmento(segmento: any) {
    if (segmento && segmento.id != this.segmento) {
      return segmento.icon + '-outline'
    }
    return segmento.icon;
  }

  getImagenUsuario() {
    if (this.usuario && this.usuario.opcionesModal) {
      if (this.usuario && this.usuario?.opcionesModal) {
        let opcionBasica = this.getOpcionBasica(this.usuario?.opcionesModal, 'General', 'FOTO_AJUSTES_USUARIO');
        if (opcionBasica && opcionBasica.valor && opcionBasica.valor.length > 0) {
          //console.log('opcionBasica.valor[0]', opcionBasica.valor[0])
          return this.appData.PATH_IMG_AJUSTES + opcionBasica.valor[0];
        }
      }
    }
    return this.appData.PATH_IMG_AJUSTES + this.appData.IMG_INFO[0].name
  }

  getOpcionBasica(opcionesModal: OpcionesModal, opcionGrupoId: string, opcionBasicaId: string) {
    let grupoOpciones: GrupoOpciones = (opcionesModal.opciones as any).find((go: any) => go.etiqueta == opcionGrupoId);
    let opcionBasica: OpcionBasica = (grupoOpciones.opciones as any)?.find((o: any) => o.id == opcionBasicaId);
    return opcionBasica;
  }

  agregarUsername(username: string) {
    this.listUsuarioModal.push(username);
  }
  
  eliminarUsername(username: string) {
    const listaArreglo = Array.from(this.listUsuarioModal);
    const index = listaArreglo.indexOf(username);
    if (index !== -1) {
      listaArreglo.splice(index, 1);
      this.listUsuarioModal = new Set(listaArreglo);
    }
  }

  async agregarNuevaTareaInfo() {
    if (this.usuario && this.usuario.id && this.textoModal && this.diasRepeticionModal && this.listUsuarioModal) {
      const msj = await this.dataManagementService.añadirEstadoMsj(undefined, "TAREAS", undefined, 
      this.textoModal, undefined, "INFO", this.diasRepeticionModal, this.listUsuarioModal);
      this.actualizarTareas();
      this.cancelModal();
    } else {
      const object: any = { usuarios: this.listUsuarioModal, textoModal: this.textoModal, diasRepeticionModal: this.diasRepeticionModal }
      this.utils.mostrarAlerta('No se pudo agregarNuevaTareaInfo', JSON.stringify(object));
    }
  }

  cambiarMesCalendario(){
    console.log('cambiarMesCalendario');
    let monthText: any = document.querySelector('ion-datetime')?.shadowRoot?.querySelector('.calendar-header')?.querySelector('ion-label');
    // Este codigo lo usamos para poner la primera letra del titulo de calendario en Mayuscula
    if (monthText && monthText.textContent) {
      monthText.style['text-transform'] = 'capitalize';
    }
    console.log('monthText', monthText?.textContent)


    return monthText;
  }

  initMonthObserver() {
    let monthText = this.cambiarMesCalendario()
    const elementToObserve = document.querySelector('ion-datetime')?.shadowRoot
    if (elementToObserve) { 
      const observer = new MutationObserver(async (mutationsList, observer) => {
        let monthString = monthText?.textContent;
        let parts = monthString?.split(" ")
        if (parts && parts[2] && parts[0]) {
          const mes =  this.utils.obtenerNumeroMes(parts[0]); 
          if (mes) {
            const anyo = Number(parts[2]);
            await this.getEventos(mes , anyo);
          } else {
            const fechaHoy = new Date();
            const mes = fechaHoy.getMonth() + 1; 
            const anyo = fechaHoy.getUTCFullYear();
            await this.getEventos(mes , anyo);
          }
        } else {
          const fechaHoy = new Date();
          const mes = fechaHoy.getMonth() + 1; 
          const anyo = fechaHoy.getUTCFullYear();
          await this.getEventos(mes , anyo);
        }        
      })
      const observerConfig = {
        childList: true, 
        subtree: true, 
      };
      observer.observe(elementToObserve, observerConfig)
    }
  }

  cambiarColorEstadoEvento(color?: any) {
    if (color) {
      if (this.isModificarCitaModalOpen) {
        this.eventoParaModificar.imgMsjPath = color.color;
      }

      if (this.isCitaModalOpen) {
        this.colorEstadoEvento = color.color;
      }      
    } else {
      this.colorEstadoEvento = 'var(--ion-color-secondary)';
    }
    this.isCitaColoresModalOpen = false;
  }

  asignarColorIconoSegmento(segmento: any) {
    let CssStyles = null;
    if (this.segmento == segmento.id) {
      CssStyles = {        
        'color': 'var(--ion-color-dark)'
      };
    } else {
      CssStyles = {
        'color': 'var(--ion-color-primary-contrast)'
      };
    }
    return CssStyles;
  }

  asignarColorEstadoEventoNuevo(color?: any) {
    let CssStyles = null;
    if (color) {
      CssStyles = {        
        'background-color': color.color
      };
    } else {
      CssStyles = {
        'background-color': this.colorEstadoEvento
      };
    }
    return CssStyles;
  }

  asignarColorEstadoEvento(color?: any) {
    let CssStyles = null;
    if (color) {
      CssStyles = {        
        'background-color': color
      };
    } else {
      CssStyles = {
        'background-color': this.colorEstadoEvento
      };
    }
    return CssStyles;
  }

  abrirPopOverColores() {
    this.isCitaColoresModalOpen = true;  
  }

  labelDiaSeleccionado(diaSeleccionado: any) {
    if(diaSeleccionado) {
      const fechaString = (diaSeleccionado as String).replace('T', ' ').split('+')[0];
      const [fechaParte, horaParte] = fechaString.split(' ');
      const [anio, mes, dia] = fechaParte.split('-');
      if (this.utils.isToday(diaSeleccionado)) {
        return 'Hoy ' + '(' + dia + ' de ' + this.utils.obtenerNombreMes(Number(mes) - 1) + ', '+  anio + ')';
      } else if (this.utils.isTomorrow(diaSeleccionado)) {
        return 'Mañana ' + '(' + dia + ' de ' + this.utils.obtenerNombreMes(Number(mes) - 1) + ', '+  anio + ')';
      } else {
        return dia + ' de ' + this.utils.obtenerNombreMes(Number(mes) - 1) + ', '+  anio;
      }
    }
    return 'Error fecha'
  }

  mostrarDiaSeleccionado() {
    if(this.diaSeleccionado) {
      const fechaString = (this.diaSeleccionado as String).replace('T', ' ').split('+')[0];
      const [fechaParte, horaParte] = fechaString.split(' ');
      const [anio, mes, dia] = fechaParte.split('-');
      const fecha = new Date(Number(anio), Number(mes) - 1, Number(dia));
      const timestamp = fecha.getTime();
      return this.diaSeleccionado;
    } 
  }

  isSeletedDay(fecha: any) { 
    if (this.diaSeleccionado) {
      const fechaString = (this.diaSeleccionado as String).replace('T', ' ').split('+')[0];
      const [fechaParte, horaParte] = fechaString.split(' ');
      const [anio, mes, dia] = fechaParte.split('-');
      const fechaSeletedDay = new Date(Number(anio), Number(mes) - 1, Number(dia));
    
        const fechaDate = new Date(fecha);
        return fechaDate.getUTCFullYear() === fechaSeletedDay.getUTCFullYear() &&
        fechaDate.getMonth() === fechaSeletedDay.getMonth() &&
        fechaDate.getDate() === fechaSeletedDay.getDate(); 
    }   
    return;
  }

  initFechasMarcadas() {
    console.log('initFechasMarcadas');
    if (this.eventosYCitas) {
      this.fechasMarcadas = this.eventosYCitas.map( m => {
        if (m.imgMsjPath) {
          return {
            date: this.datePipe.transform(m.fecha, 'yyyy-MM-dd'),
            textColor: 'var(--ion-color-secondary-contrast)',
            backgroundColor: m.imgMsjPath
          }
        } else {
          return {
            date: this.datePipe.transform(m.fecha, 'yyyy-MM-dd'),
            textColor: 'var(--ion-color-secondary-contrast)',
            backgroundColor: 'var(--ion-color-secondary)',
          }
        }

      });
      
    }
    return;
  }

  ionChange(event: any) {
    console.log('ionChange');
    if (event.detail.value) {
      this.diaSeleccionado = event.detail.value;
    }
    this.actualizarEventosYCitas();
  }

  cancelModal() {
    this.isCitaModalOpen = false;
    this.isModificarCitaModalOpen = false;
    this.isTareaModalOpen = false;
    this.textoModal = this.usernameModal = this.diasRepeticionModal = null;
    this.colorEstadoEvento = 'var(--ion-color-secondary)';
    this.eventoParaModificar = new EstadoMsj();
    this.listUsuarioModal = ['puertocho', 'alba'];
    this.modal.dismiss(null, 'cancel');
  }

  segmentoDispositivosCambiado(event: any) {
    const value = event?.detail?.value;
    // Aqui hacemos algo como buscar los datos del dispositivo y asignarselo a segmentoDispositivo
    this.segmentoDispositivo = this.dispositivos.find(d => d.id == value);
  }

  public actualizarNumeroNotificaciones() {
    let segmento = this.segmentos.find(s => s.id == 'NOTIFICACIONES')
    const tamNotificacion = this.notificaciones.length;
    segmento.badge.value = tamNotificacion > 0 ? this.notificaciones.length: null;
  }

  async cerrarNotificacion(event: any) {
      const noVisible: boolean = await this.dataManagementService.cerrarNotificacion(event.item.id);
      if (noVisible) {
        const indiceObjetoAReemplazar = this.notificaciones?.findIndex(m => m.id === event.item.id);
        if (indiceObjetoAReemplazar !== undefined && indiceObjetoAReemplazar !== -1) {
          this.notificaciones?.splice(indiceObjetoAReemplazar, 1);
          this.actualizarNumeroNotificaciones();
        }
      }
  }

  async redireccionPorNotificacion(event: any) {
    await this.navCtrl.navigateForward(event.item.redireccion);
  }

  async notificacionesEvent(event: any) {
    if (event.accion == "cerrarNotificacion") {
      await this.cerrarNotificacion(event);
    }
    if (event.accion == "redireccionPorNotificacion") {
      await this.redireccionPorNotificacion(event);
    }
  }

  calendarioEvent(event: any) {
    console.log('calendarioEvent', event);
  }

  async tareasEvent(event: any) {
    if (event.accion == "terminarTareaPorHacer") {
      if (event.checked && this.usuario && this.usuario.id) {
        const msj = await this.dataManagementService.terminarTareaPorHacer(this.usuario.id, event.item.id);
        console.log('rm', msj);
        this.actualizarTareas();
      } else {
        this.utils.mostrarAlerta('Fallo Tarea, no se ha podido finalizar', JSON.stringify(event.item));
      }
    }
    if (event.accion == "agregarTareaPorHacer") {
      if (event.item && this.usuario && this.usuario.id) {
        const msj = await this.dataManagementService.agregarTareaPorHacer(this.usuario.id, event.item.id);
        this.actualizarTareas();
        this.cancelModal();
      } else {
        this.utils.mostrarAlerta('Fallo Tarea, no se ha podido añadir', JSON.stringify(event.item));
      }
    }

  }

  ionChangeModificarFecha(event: any) {
    if (event && event.detail.value) {

      console.log('event', event.detail.value)
  
    }
  }

  async modificarCitaOTarea() {
    console.log('this.eventoParaModificar', this.eventoParaModificar);
    const estadoMsjGuardado: EstadoMsj = await this.dataManagementService.modificarCitaOTarea(this.eventoParaModificar);
    console.log('estadoMsjGuardado', estadoMsjGuardado);
    this.eventosYCitas = this.eventosYCitas.map((estadoMsj) => {
      if (estadoMsj.id === estadoMsjGuardado.id) {
        return estadoMsjGuardado;
      } else {
        return estadoMsj;
      }
    });

    this.cancelModal()
  }

  async agregarCitaOTarea() {
    const fechaString = (this.diaSeleccionado as String).replace('T', ' ').split('+')[0];
    const [fechaParte, horaParte] = fechaString.split(' ');
    const [anio, mes, dia] = fechaParte.split('-');
    const [hora, minutos, segundos] = horaParte.split(':');
    const fecha = new Date(Number(anio), Number(mes) - 1, Number(dia), Number(hora), Number(minutos), Number(segundos));
    const timestamp = fecha.getTime();
    
    if (this.textoModal) {
      if (this.usuario && this.usuario.id) {        
        let estadoMsj: EstadoMsj[] = await this.dataManagementService.añadirEstadoMsj(
          this.usuario.id,
          'CALENDARIO',
          String(timestamp),
          this.textoModal,
          this.colorEstadoEvento
        );
        if (estadoMsj) {
          this.eventosYCitas.push(estadoMsj)
        }
        await this.actualizarEventosYCitas();
        this.cancelModal();        
      }
    } else {
      this.utils.mostrarAlerta('Es necesario indicar una descripción');
    }
  }                                                                                                                   

  abrirCitaOTareaModal(segmento: string) {
    if (segmento == 'CALENDARIO') {
      this.abrirCitaOEventoModal()
    } else if (segmento == 'TAREAS') {
      this.abrirTareaModal()
    }
  }

  abrirTareaModal() {
    this.isTareaModalOpen = true;
  }
  
  abrirCitaOEventoModal() {
    this.isCitaModalOpen = true;
  }

  onWillDismiss(event: Event) {
    this.cancelModal();
  }

  onWillDismissModificarCita(event: Event) {
    this.eventsService.publish('modificarItemCalendario', { modificar: false, id: null});
  }

  onWillDismissColoresModal(event: Event) {
    this.isCitaColoresModalOpen = false;
  }
  
  public async getEventos(mes: number, anyo: number) {
    if (this.usuario && this.usuario.id) {
      this.eventosYCitas = await this.dataManagementService.getEventosYCitas(this.usuario.id, mes, anyo);
      this.actualizarEventosYCitas();
    }
  }

  async actualizarTareas() {
    if (this.usuario && this.usuario.id) {
      this.tareas = await this.dataManagementService.getTareas(this.usuario.id);
      this.tareasPorHacer = this.tareas.filter(e => e.estado == 'POR_HACER');
      this.tareasInfo = this.tareas.filter(e => e.estado == 'INFO');
    } else {
      this.mostrarAlertaErrorUsuario();
    }
  }

  async actualizarEventosYCitas() {
    this.modificacionesVisualesEnCalendario();
    await this.initFechasMarcadas();
    this.eventosYCitasToday = this.eventosYCitas.filter(e => this.utils.isToday(e.fecha)).sort(function(a, b){return a.fecha - b.fecha});
    this.eventosYCitasTomorrow = this.eventosYCitas.filter(e => this.utils.isTomorrow(e.fecha)).sort(function(a, b){return a.fecha - b.fecha});
    this.eventosYCitasDiaSeleccionado = this.eventosYCitas.filter(e => this.isSeletedDay(e.fecha)).sort(function(a, b){return a.fecha - b.fecha});
    this.cambiarMesCalendario();
  }

  async segmentoCambiado(event: any) {
    this.loading = true;
    this.disabledChangePageDatetime = true;
    this.segmento = event?.detail?.value;
    if (this.usuario && this.usuario.id) {
      if (this.segmento === 'NOTIFICACIONES') {
        this.notificaciones = await this.dataManagementService.getNotificaciones(this.usuario.id);
        this.notificaciones = this.notificaciones.sort(function(a, b){return a.fecha - b.fecha});
        this.actualizarNumeroNotificaciones();
        this.loading = false;
      }
      
      if (this.segmento === 'CALENDARIO') {
        const fechaHoy = new Date();
        const mes = fechaHoy.getMonth() + 1; 
        const anyo = fechaHoy.getUTCFullYear();
        await this.getEventos(mes, anyo);
        setTimeout(async () => {
          await this.initMonthObserver();
          this.disabledChangePageDatetime = false;
        }, 500);
        this.loading = false;
      }

      if (this.segmento === 'TAREAS') {
        await this.actualizarTareas() ;
        this.loading = false;
      }

      if (this.segmento === 'DISPOSITIVOS') {
        console.log('this.dispositivos', this.dispositivos)
        this.loading = false;
      }
    } else {
      await this.mostrarAlertaErrorUsuario();
      this.loading = false;
    }
  }

  async ngOnInit() {
    this.loading = true;
    this.usuario = await this.dataManagementService.getUsuario();
    this.initMonthObserver();
    if (this.usuario && this.usuario.id) {
      this.notificaciones = await this.dataManagementService.getNotificaciones(this.usuario.id);
      this.notificaciones = this.notificaciones.sort(function(a, b){return a.fecha - b.fecha});
      this.actualizarNumeroNotificaciones();
    } else {
      await this.mostrarAlertaErrorUsuario();
    }
    this.loading = false;
  }

  private async mostrarAlertaErrorUsuario() {
    await this.utils.mostrarAlerta('Error al leer el usuario', 'status.page.ts -> mostrarAlertaErrorUsuario()');
  }

  public mostrarSegmento(segmento: string) {
    //console.log('segmento', segmento)
    return true;
  }

}
