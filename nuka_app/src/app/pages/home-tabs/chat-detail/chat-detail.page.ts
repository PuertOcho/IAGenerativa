import { Component, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AnimationController, IonButton, IonContent, ModalController } from '@ionic/angular';
import { BehaviorSubject, SubscriptionLike } from 'rxjs';
import { debounceTime, tap } from 'rxjs/operators';
import { NukaChat, NukaMsj, OpcionBasica, OpcionesModal, User } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { VoiceRecorder, RecordingData, GenericResponse } from 'capacitor-voice-recorder';
import { UtilsService } from 'src/app/services/utils.service';
import { FilePicker } from '@capawesome/capacitor-file-picker';
import { EventsService } from 'src/app/services/events.service';
import { OptionsModalComponent } from 'src/app/components/options-modal/options-modal.component';
import { ConfigService } from 'src/app/config/config.service';
import { AppDataModel } from 'src/app/app.data.model';

@Component({
  selector: 'app-chat-detail',
  templateUrl: './chat-detail.page.html',
  styleUrls: ['./chat-detail.page.scss']
})
export class ChatDetailPage implements OnInit, OnDestroy, OnChanges {

  private animation: Animation | any;
  usuario: User | undefined;
  nukaChat: NukaChat | any;
  @Input() messages: NukaMsj[] | undefined;

  messageSended: NukaMsj | undefined;
  chatId!: string;
  imageBg = 'chat-bg';
  texto: string = '';

  recordTime: number = 0;
  audio: any;
  msDuration: number | undefined;
  audioStatus: string = 'pause';
  contenidoFiles: any[] = [];
  messageControl: FormControl = new FormControl('', [
    Validators.required
  ]);
  loading: boolean = true;
  isPageScrolling = true;
  isAllowScrollEvents = true;
  isAllowScroll = true;
  scrolling: BehaviorSubject<boolean> = new BehaviorSubject(false);
  subscriptions: SubscriptionLike[] = [];
  esperandoRespuestaMensaje: boolean = false;
  numMsjPorRecarga: number;
  redirectMsjId: string | null = '';
  opcionesRapidasCompleta = false;
  reproducirAudioAutomaticamente: boolean = false;
  isTyping: boolean = false;

  // Botones flotantes
  mostrarBotonFlotanteAjustesRapidos: boolean = false;
  mostrarBotonFlotanteActualizar: boolean = false;
  mostrarBotonFlotanteArbolDocumentos: boolean = false;
  
  // Borrado de mensajes
  borrarMensajes: boolean = false;
  
  mostrarBarraBusqueda: boolean = false;
  loadingArbolArchivos: boolean = false;

  contenido: HTMLElement | null | undefined;

  usarDirectoriosObsidian: boolean | undefined;

  @ViewChild('content', { static: false }) content: IonContent | any;
  @ViewChild('popover') popover: any;
  @ViewChild('micButton', { static: true }) micButton: IonButton | undefined;

  constructor(
    private route: ActivatedRoute,
    private dataManagementService: DataManagementService,
    public utils: UtilsService,
    public eventsService: EventsService,
    public modalCtrl: ModalController,
    private animationCtrl: AnimationController,
    public router: Router,
    public config: ConfigService,
    public appData: AppDataModel
  ) {

    this.numMsjPorRecarga = this.config.config().numMsjPorRecarga;

    this.route.params.subscribe(params => {
      this.chatId = params['id'];
      this.redirectMsjId = this.route.snapshot.queryParamMap.get('redirectMsjId');
    });

    this.eventsService.subscribe('esperandoRespuestaMensaje', async bool => {
      this.esperandoRespuestaMensaje = bool;
      if (!bool) {
        setTimeout(async () => {
          await this.scrollToBottom(300);
        }, 300)
      }
    });

    this.eventsService.subscribe('reenviarNukaMsj', async nukaMsj => {
      console.log('reenviarNukaMsj msj', nukaMsj);
      this.reenviarNukaMsj(nukaMsj);
    });

    this.eventsService.subscribe('actualizarOpcionesAjustes', () => {
      this.actualizarOpcionesAjustes();
    });

    this.eventsService.subscribe('recibirNuevoMsj', async (nuevoNukaMsj: NukaMsj) => {
      this.messages = this.messages?.concat(nuevoNukaMsj);
      this.eventsService.publish('esperandoRespuestaMensaje', false);
    });

    this.eventsService.subscribe('borrarMensajes', async (accionBorrarMensajes: boolean) => {
      this.borrarMensajes = accionBorrarMensajes;
    });
  }

  public autoGrowTextZone(e: any) {
    e.target.style.height = "0px";
    e.target.style.height = (e.target.scrollHeight + 10)+"px";
    
  }

  public iniUsarDirectoriosObsidian() {
    if (this.nukaChat) {
      this.usarDirectoriosObsidian = this.nukaChat && this.nukaChat.arbolDocumentos && this.nukaChat.arbolDocumentos.usarArbolDocumentos;
      return this.usarDirectoriosObsidian;
    }
    return null;
  }

  ionViewDidEnter() {
    this.actualizarOpcionesAjustes();
    this.iniUsarDirectoriosObsidian();
  }

  ionViewWillLeave() {
    this.mostrarBotonFlotanteAjustesRapidos = false;
  }

  public getClassSummary() {
    return this.isTyping ? 'SummaryChat typing' : 'SummaryChat';
  }

  async toggleCheckboxDirectorios() {
    this.loadingArbolArchivos = true;
    let chat: NukaChat = await this.dataManagementService.usarDirectoriosObsidian(this.chatId, !this.usarDirectoriosObsidian).finally(() => {
      this.loadingArbolArchivos = false;
    });
    console.log('toggleCheckboxDirectorios', chat);
    if (chat) {
      this.nukaChat = chat;
      this.usarDirectoriosObsidian = !this.usarDirectoriosObsidian;
    }
  }

  actualizarOpcionesAjustes() {
    if (this.nukaChat) {
      const reproducirAudioAutomaticamenteOpcion = this.utils.getOpcionBasicaById(this.nukaChat.opcionesModal, 'AUDIO_REPRODUCCION_AUTOMATICA');
      if (reproducirAudioAutomaticamenteOpcion && reproducirAudioAutomaticamenteOpcion.valor &&
        reproducirAudioAutomaticamenteOpcion.valor.length > 0) {
        this.reproducirAudioAutomaticamente = Boolean(reproducirAudioAutomaticamenteOpcion.valor[0]);
      }

      const boolmostrarBotonFlotanteAjustesRapidos = this.utils.getOpcionBasica(this.nukaChat.opcionesModal, 'AJUSTES RAPIDOS', 'AJUSTES_RAPIDOS_MOSTRAR_AJUSTES');
      if (boolmostrarBotonFlotanteAjustesRapidos && boolmostrarBotonFlotanteAjustesRapidos.valor && boolmostrarBotonFlotanteAjustesRapidos.valor.length > 0) {
        this.mostrarBotonFlotanteAjustesRapidos = JSON.parse(boolmostrarBotonFlotanteAjustesRapidos.valor[0]);
      }

      const boolmostrarBotonFlotanteActualizar = this.utils.getOpcionBasica(this.nukaChat.opcionesModal, 'AJUSTES RAPIDOS', 'AJUSTES_RAPIDOS_MOSTRAR_ACTUALIZAR');
      if (boolmostrarBotonFlotanteActualizar && boolmostrarBotonFlotanteActualizar.valor && boolmostrarBotonFlotanteActualizar.valor.length > 0) {
        this.mostrarBotonFlotanteActualizar = JSON.parse(boolmostrarBotonFlotanteActualizar.valor[0]);
      }

      const boolmostrarBotonFlotanteArbolDocumentos = this.utils.getOpcionBasica(this.nukaChat.opcionesModal, 'AJUSTES RAPIDOS', 'AJUSTES_RAPIDOS_MOSTRAR_ARBOL_DOCUMENTOS');
      if (boolmostrarBotonFlotanteArbolDocumentos && boolmostrarBotonFlotanteArbolDocumentos.valor && boolmostrarBotonFlotanteArbolDocumentos.valor.length > 0) {
        this.mostrarBotonFlotanteArbolDocumentos = JSON.parse(boolmostrarBotonFlotanteArbolDocumentos.valor[0]);
      }

      
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    throw new Error('Method not implemented.');
  }

  public async abrirConfiguracionAjustes() {
    if (this.nukaChat?.opcionesModal) {
      const modal = await this.modalCtrl.create({
        component: OptionsModalComponent,
        componentProps: {
          'esAjustesChat': true,
          'opcionesModal': this.nukaChat?.opcionesModal,
          'id': this.nukaChat.id
        }
      });

      await modal.present();

      modal.onWillDismiss().then((data) => {
        if (this.nukaChat && data?.data?.salida) {
          this.nukaChat.opcionesModal = data?.data?.salida as OpcionesModal;
          this.datosParaActualizarVista(data?.data?.salida as OpcionesModal);
        }
      });
    }
  }

  public getImagenChat() {
    return this.utils.getImagenChat(this.nukaChat);
  }

  public async datosParaActualizarVista(opcionesModal: OpcionesModal) {
    if (opcionesModal && opcionesModal.opciones) {
      if (this.nukaChat!.summary != 'GENERAL') {
        let obNombre: OpcionBasica = this.utils.getOpcionBasica(opcionesModal, 'GENERAL', 'GENERAL_NOMBRE_CHAT');
        console.log('obNombre', obNombre);
        if (obNombre && obNombre.valor != null) {
          this.nukaChat!.summary = obNombre.valor && obNombre.valor.length > 0 ? obNombre.valor[0] : null;
        }
      }

      let obTags = this.utils.getOpcionBasica(opcionesModal, 'GENERAL', 'GENERAL_TAGS');
      if (obTags && obTags.valor != null) {
        this.nukaChat!.tags = obTags.valor;
      }

      await this.eventsService.publish('actualizarChat', this.nukaChat);

    } else {
      await this.utils.mostrarAlerta('Error al actualizar datos de la vista', 'datosParaActualizarVista');
    }
  }

  public obtenerValorOpcionesModal(opciones: any[], id: string): any | null {
    for (const opcion of opciones) {
      if (opcion.opciones) {
        for (const subOpcion of opcion.opciones) {
          if (subOpcion.id === id) {
            return subOpcion.valor;
          }
        }
      }
    }
    return null; // Retorna null si no se encuentra el valor
  }

  async requestMicrophonePermission(): Promise<void> {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      return new Promise(async (resolve, reject) => {
        if (!this.utils.isAndroid()) {
          navigator.mediaDevices.getUserMedia({ audio: true })
            .then((stream) => {
              console.log('Permiso de micrófono concedido');
              resolve();
              // Aquí puedes realizar las acciones necesarias para acceder al micrófono con el stream de audio
            })
            .catch((error) => {
              console.error('Error al solicitar permiso de micrófono:', error);
              reject();
            });
        } else {
          const a = await VoiceRecorder.requestAudioRecordingPermission();
          resolve()
        }
      });
    } else {
      console.error('El navegador no admite el acceso al micrófono');
    }
  }

  onButtonPressed() {
    this.requestMicrophonePermission();
    // Lógica cuando se pulsa el botón
    console.log('Botón presionado');
    VoiceRecorder.startRecording()
      .then((result: GenericResponse) => {
        console.log(result.value);
        this.audio = result;
      })
      .catch((error: any) => console.log(error))

  }

  onButtonReleased() {
    // Lógica cuando se suelta el botón
    console.log('Botón liberado');
    VoiceRecorder.stopRecording()
      .then((result: RecordingData) => console.log(result.value))
      .catch((error: any) => console.log(error))
  }

  public async animacionGrabando() {

    if (this.micButton) {
      const boton = this.animationCtrl
        .create()
        .addElement(this.micButton.form as Element)
        .duration(2000)
        .keyframes([
          { offset: 0, width: '80px' },
          { offset: 0.72, width: 'var(--width)' },
          { offset: 1, width: '240px' },
        ]);

      this.animation = this.animationCtrl
        .create()
        .duration(2000)
        .iterations(Infinity)
        .addAnimation([boton]);

      this.animation.play();

    }

  }

  async onButtonPressed_StartRecording() {
    await this.requestMicrophonePermission();
    this.audioStatus = 'record';
    this.animacionGrabando();

    VoiceRecorder.startRecording()
      .then((result: GenericResponse) => {
        const idInteval = setInterval(() => {
          if (this.audioStatus == 'pause') {
            clearInterval(idInteval);
          } else {
            this.recordTime += 100;
          }
        }, 100)
      })
      .catch((error: any) => console.log(error))
  }

  onButtonReleased_AudioAndFile() {
    VoiceRecorder.stopRecording()
      .then((result: RecordingData) => {
        this.audioStatus = 'pause';
        this.recordTime = 0;
        const base64Sound = result.value.recordDataBase64;
        const mimeType = result.value.mimeType
        this.msDuration = result.value.msDuration
        console.log('msDuration', this.msDuration)
        this.audio = new Audio(`data:${mimeType};base64,${base64Sound}`)
      }).catch((error: any) => {
        console.log(error);
        this.deleteCurrentAudio();
      })
  }

  deleteSpecificFile(contenidoFile: any) {
    const indexToDelete = this.contenidoFiles.findIndex(file => file.name === contenidoFile.name);
    this.contenidoFiles.splice(indexToDelete, 1);
  }

  selectIconName(contenidoFile: any) {
    if (contenidoFile.mimeType.includes('audio')) {
      return 'musical-notes-outline'
    } else if (contenidoFile.mimeType.includes('video')) {
      return 'videocam-outline'
    } else if (contenidoFile.mimeType.includes('image')) {
      return 'image-outline'
    } else if (contenidoFile.mimeType.includes('text') || contenidoFile.mimeType.includes('pdf')) {
      return 'document-text-outline'
    }
    return 'document-outline'
  }

  async adjuntar_AudioAndFile() {
    const result = await FilePicker.pickFiles({
      multiple: true,
    });
    this.contenidoFiles = result && result.files ? result.files : [];
  }

  onButtonReleased_OnlyAudio() {
    VoiceRecorder.stopRecording()
      .then((result: RecordingData) => {
        this.audioStatus = 'pause';
        this.recordTime = 0;
        const base64Sound = result.value.recordDataBase64;
        const mimeType = result.value.mimeType
        this.audio = new Audio(`data:${mimeType};base64,${base64Sound}`)
        this.enviarNukaMsj();
      }).catch((error: any) => {
        this.audioStatus = 'pause';
        this.recordTime = 0;
        console.log(error);
        this.deleteCurrentAudio();
      })
  }

  deleteCurrentAudio() {
    this.audio = null;
    this.msDuration = undefined;
  }

  async playCurrentAudio() {
    this.audioStatus = 'play';
    this.audio.play();
    const idInteval = setInterval(() => {
      if (this.audio.paused) {
        this.audioStatus = 'pause';
        clearInterval(idInteval);
      }
    }, 250)
  }

  async pauseCurrentAudio() {
    this.audioStatus = 'pause';
    this.audio.pause()
  }

  public async onIonInfinite(event: any) {
    await this.agregarNuevosMsjs(event);
  }

  async getContentSize(): Promise<number[]> {
    return this.content.getScrollElement().then((scrollElement: HTMLElement) => {
      const scrollHeight = scrollElement.scrollHeight;
      const clientHeight = scrollElement.clientHeight;
      return [scrollHeight, clientHeight];
    });
  }

  public async setScroll() {
    setTimeout(async () => {
      let scrollGuardado: number | undefined = undefined;
      if (this.redirectMsjId) {
        const offsetTop = document.getElementById(this.redirectMsjId)?.offsetTop;
        const contentSize = await this.getContentSize()

        if (offsetTop && contentSize) {
          await this.content?.scrollToPoint(0, contentSize[0] - contentSize[1] - offsetTop - 100, 1000);
          this.redirectMsjId = null;
        } else {
          await this.content?.scrollToBottom(0);
        }
      }
    }, 300);

  }

  public async sincronizarChat() {
    this.usuario = await this.dataManagementService.getUsuario();
    const chats: NukaChat[] = await this.dataManagementService.getNukaChats(this.usuario.id as any);
    this.nukaChat = chats.find(c => c.id == this.chatId);
    if (this.chatId) {
      console.log('this.messages', this.messages)
      const mensagges = await this.dataManagementService.getMessages([this.chatId], this.numMsjPorRecarga, String(Date.now()));
      this.messages = mensagges;
    }
  }

  dataInit() {
    return new Promise<boolean>(async (resolve) => {
      this.loading = true;
      this.usuario = await this.dataManagementService.getUsuario();
      //const localChats = await this.dataManagementService.getLocalChats();
      //this.nukaChat = localChats.find(c => c.id == this.chatId);
      const chats: NukaChat[] = await this.dataManagementService.getNukaChats(this.usuario.id as any);
      this.nukaChat = chats.find(c => c.id == this.chatId);
      this.iniUsarDirectoriosObsidian();
      if (this.chatId) {
        console.log('this.messages', this.messages)
        const mensagges = await this.dataManagementService.getMessages([this.chatId], this.numMsjPorRecarga, String(Date.now()));
        this.messages = mensagges;
      }

      setTimeout(() => {
        this.loading = false;
        
        
        //this.abrirArbolDirectorios(); // TEST, quitar cunado se termine el componente directorios-obsidian
        
        resolve(true)
      }, 800);
    });
  }

  async ngOnInit(): Promise<void> {
    this.subscriptions.push(
      this.scrolling.pipe(
        tap(scroll => {
          if (scroll) {
            this.isPageScrolling = scroll;
          }
        }),
        debounceTime(1000),
      ).subscribe(res => this.isPageScrolling = res)
    );

    this.dataInit()
      .then(async () => {
        //await this.eventsService.publish('esperandoRespuestaMensaje', false);
        //console.log('esperandoRespuestaMensaje 1 ');
        // SI hay en el registro local una ultima posicion asignada al chat, entonces se va a esa pos sino a abajo del todo

      }).then(async () => {
        this.getContentSize();
        this.actualizarOpcionesAjustes();
        setTimeout(async () => await this.comprobarScroll(), 500);
      });

  }
  
  async logScrolling(event: any) {
    if (this.nukaChat && this.messages) {
      this.nukaChat.ultimaPosicionDeChat = event?.detail?.currentY;
      this.nukaChat.numeroMensajes = this.messages.length;
    }
  }

  initNukaMsjPeticion(nukaMsj?: NukaMsj): NukaMsj {
    let nukaMsjPeticion: NukaMsj = !nukaMsj ? new NukaMsj() : nukaMsj;
    nukaMsjPeticion.id = String(Date.now()) + '_' + this.utils.generarIdAleatorio(24);
    nukaMsjPeticion.fecha = String(Date.now());

    if (!nukaMsj) {
      nukaMsjPeticion.texto = this.texto;
      nukaMsjPeticion.tipoMsj = "PETICION";
      nukaMsjPeticion.usuarioId = this.usuario?.id;
      nukaMsjPeticion.chatId = this.chatId;
      nukaMsjPeticion.respondido = false;
    }
    return nukaMsjPeticion;
  }

  async reenviarNukaMsj(nukaMsj: NukaMsj) {
    this.initNukaMsjPeticion(nukaMsj); 
    await this.eventsService.publish('esperandoRespuestaMensaje', true);
    let respuesta: NukaMsj[] = await this.dataManagementService.nukaMsjPeticion(nukaMsj, null).finally(async () => {
      this.sincronizarChat();
      await this.eventsService.publish('esperandoRespuestaMensaje', false);
    });
    await this.responderNukaMensaje(respuesta);
  }

  responderNukaMensaje(respuesta: NukaMsj[] | any) {
    let peticion = respuesta.find((m: NukaMsj) => m.tipoMsj == 'PETICION');
    const indiceObjetoAReemplazar = this.messages?.findIndex(m => m.id === peticion?.id);
    if (indiceObjetoAReemplazar != undefined && respuesta && indiceObjetoAReemplazar !== -1) {
      this.messages?.splice(indiceObjetoAReemplazar, 1, ...respuesta);
      this.messages = [...this.messages as any];
    }
  }

  async enviarNukaMsj(): Promise<void> {
    if (this.texto || this.audio) {
      if (this.content) {
        await this.scrollToBottom(0);
      }

      await this.eventsService.publish('esperandoRespuestaMensaje', true);
      this.usuario = this.usuario ? this.usuario : await this.dataManagementService.getUsuario();

      if (this.usuario) {
        let nukaMsjPeticion: NukaMsj = this.initNukaMsjPeticion();

        if (this.audio) {
          (await VoiceRecorder.getCurrentStatus()).status !== 'NONE' ? await VoiceRecorder.stopRecording() : null;

          const file: File | null = await this.utils.convertirAudio(this.audio, nukaMsjPeticion.id);

          if (file) {
            if (file.type == "audio/aac") {
              nukaMsjPeticion.audioResource = "audioAac"
            } else if (file.type == "audio/webm") {
              nukaMsjPeticion.audioResource = "audioWebm"
            }
            if (nukaMsjPeticion.audioResource) {
              nukaMsjPeticion.needAudio = true;
              nukaMsjPeticion.audioFile = file;
            } else {
              this.utils.presentToast('Formato de audio no valido + ' + file.type, 'middle', 'danger', 3000)
            }
          }
          this.audio = null;
        }

        this.messages = this.messages?.concat(nukaMsjPeticion);
        let respuesta: NukaMsj[] = await this.dataManagementService.nukaMsjPeticion(nukaMsjPeticion, this.contenidoFiles).finally(async () => {
          console.log('hemos recibido respuesta de la peticion');
          await this.eventsService.publish('esperandoRespuestaMensaje', false);
        })
        await this.responderNukaMensaje(respuesta);



        this.actualizarOpcionesAjustes();
        if (this.reproducirAudioAutomaticamente && respuesta && respuesta.some(r => r.needAudio)) {
          respuesta.forEach(async r => {

            console.log('respuesta', r);
            setTimeout(async () => {
              if (r && r.needAudio && r.audioResource && r.tipoMsj && r.tipoMsj == 'RESPUESTA') {
                const ultimoAudio = await this.utils.getResourceAudio(r, this.dataManagementService);
                await ultimoAudio.play();
              }
            }, 300);

          });
        }

        // Para generar automaticamente un titulo al chat
        const ob = this.utils.getOpcionBasica(this.nukaChat.opcionesModal, "GENERAL", "GENERAL_AUTONOMBRAR_CHAT")
        console.log('ob', ob)
        if (this.nukaChat.summary == 'Chat sin titulo' && ob && ob.valor && ob.valor[0] == 'true' ) {
          this.dataManagementService.autonombrarChat(this.chatId).then(async nuevoTitulo => {
            console.log('nuevoTitulo', nuevoTitulo);
            this.nukaChat.summary = nuevoTitulo;
            await this.eventsService.publish('actualizarChat', this.nukaChat);
            this.isTyping = true;
            setTimeout(() => {
              this.isTyping = false;
            }, 3500); 
          });

        }

      }
    }
  }

  async comprobarScroll() {
    console.log('redirectMsjId 0', this.redirectMsjId)
    await this.setScroll()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.subscriptions = [];
  }

  public async scrollToBottom(number?: number) {
    console.log('scrollToBottom');
    await this.content?.scrollToPoint(0, 0, number);
  }

  public async scrollToTop(number?: number) {
    console.log('scrollToTop');
    await this.content?.scrollToBottom(number);
  }

  public async agregarNuevosMsjs(event: any) {
    const primerMsj: NukaMsj = this.messages?.reduce((primerMensaje: NukaMsj | any, mensaje: NukaMsj | any) => {

      const fechaActual = new Date(parseInt(mensaje.fecha));
      const fechaPrimerMensaje = new Date(parseInt(primerMensaje.fecha));

      return fechaActual < fechaPrimerMensaje ? mensaje : primerMensaje;
    }, this.messages[0]); // Inicializa el primer mensaje como el primer elemento del array

    if (this.nukaChat && this.nukaChat.id) {
      if (this.redirectMsjId) {
        let nuevosMensajes: NukaMsj[] = await this.dataManagementService.getMessagesBetweenDates(this.nukaChat.id, primerMsj.id, this.redirectMsjId)
        this.concatenarNuevosMsjs(nuevosMensajes);
        this.redirectMsjId = null;
        event?.target?.complete();
      } else {
        const fecha = primerMsj && primerMsj.fecha ? primerMsj.fecha : String(Date.now());;
        let nuevosMensajes: NukaMsj[] = await this.dataManagementService.getMessages([this.nukaChat.id], this.numMsjPorRecarga, fecha);
        this.concatenarNuevosMsjs(nuevosMensajes);
      }
      event?.target?.complete();
    } else {
      event?.target?.complete();
    }
  }

  public concatenarNuevosMsjs(nuevosMensajes: NukaMsj[]) {
    console.log('nuevosMensajes', nuevosMensajes);
    this.messages = nuevosMensajes?.concat(this.messages ? this.messages : []);
  }

  public handleInputBuscador(event: any) {
    console.log('handleInputBuscador', event);
  }

  public toggleMostrarBarraBusqueda() {
    this.mostrarBarraBusqueda = !this.mostrarBarraBusqueda;
  }

  public assinarEstiloIconoBusqueda() {
    let CssStyles = {
      'color': 'var(--ion-color-primary)'
    };
    if (!this.mostrarBarraBusqueda) {
      CssStyles = {
        'color': 'var(--ion-color-medium)'
      };
    }
    return CssStyles;
  }

  public cancelarAccionBorrarMensajes() {
    this.borrarMensajes = false;
    this.messages?.forEach(m => {
      m.seleccionado = false;
    });
  }

  public async borrarMensajesSeleccionados() {
    let idsMarcados = this.messages?.filter(m => m.seleccionado == true).map(m => m.id);
    if (idsMarcados && idsMarcados.length > 0) {
      const haSidoEliminado = await this.dataManagementService.eliminarNukaMensajes(idsMarcados);
      if (haSidoEliminado) {
        this.messages = this.messages?.filter(m => (idsMarcados?.indexOf(m.id) == -1));
        this.cancelarAccionBorrarMensajes();
      } else {
        this.utils.mostrarAlerta("ERROR borrado de mensajes", "No se ha borrado ningún mensaje");
        this.cancelarAccionBorrarMensajes();
      }
    } else {
      this.utils.mostrarAlerta("Aviso borrado de mensajes", "No se ha seleccionado ningún mensaje para borrar");
      this.cancelarAccionBorrarMensajes();
    }
  }
}
