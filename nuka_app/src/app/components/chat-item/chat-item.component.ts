import { Component, ElementRef, Input, NgModule, OnInit, ViewChild, AfterViewInit, OnChanges, Output, EventEmitter, ViewChildren, QueryList, Pipe, PipeTransform } from '@angular/core';
import { AnimationController, IonContent, IonItemSliding, NavController } from '@ionic/angular';
import { AppDataModel } from 'src/app/app.data.model';
import { NukaChat, NukaMsj } from 'src/app/models/nuka.model';
import { EventsService } from 'src/app/services/events.service';
import { Clipboard } from '@ionic-native/clipboard/ngx';
import { UtilsService } from 'src/app/services/utils.service';
import { register } from 'swiper/element/bundle';
register();

@Component({
  selector: 'app-chat-item',
  templateUrl: './chat-item.component.html',
  styleUrls: ['./chat-item.component.scss'],
})
export class ChatItemComponent implements OnChanges {
  @Input() messages: any[] = [];
  @Input() esperandoRespuestaMensaje: boolean | undefined;
  @Input() borrarMensajes: boolean | undefined;

  @Output() scrollToBottomEvent: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild('iconoAnimacionDeslizamiento', { read: ElementRef }) icon: ElementRef<HTMLIonIconElement> | any;

  animation: Animation | any;

  constructor(
    private navCtrl: NavController,
    public appData: AppDataModel,
    public eventsService: EventsService,
    private animationCtrl: AnimationController,
    private clipboard: Clipboard,
    public utils: UtilsService
  ) { }

  public toogleSeleccionadoParaBorrar(message: NukaMsj) {
    message.seleccionado = !message.seleccionado;
  }

  public onLongPress(event: any) {
    console.log('Presión larga detectada en chat-item', event);
  }
  
  ngOnInit () {
  }

  reenviarNukaMsj(message: NukaMsj) {
    this.eventsService.publish('reenviarNukaMsj', message as NukaMsj);
  }

  ngOnChanges() {
  }

  public asignarColorFondoBorrado(message: NukaMsj) {
    return message && message.seleccionado ? 'color-fondo-borrado': '';
  }

  ngClassAssign(index: number): string {
    let ngClass: string = this.messages[index + 1] ? "{'user-last-message': chats["+ String(index +1) +"].type === 'me' || last : null}": '';
    return ngClass;
  }

  checkImageItem(message: NukaMsj) {
    const contenidos = message.contenidoMsj?.find((m => ((m.tipo == 'imagenPng') || (m.tipo == 'imagenJpg') )));
    return contenidos != null
  }

  public redireccionar(pagina: string) {
    let redireccion = this.appData.redirecciones.get(pagina);
    let segmento = redireccion.segmento ? redireccion.segmento: null;
    this.navCtrl.navigateForward(redireccion.redireccion, { state: { segmento } });
  }

  public getNombreRedireccion(pagina: string): string {
    let redireccion = this.appData.redirecciones.get(pagina);
    return redireccion.etiqueta;
  }

  public async recibirNuevoMsj(nuevoNukaMsj: NukaMsj) {
    await this.eventsService.publish('recibirNuevoMsj', nuevoNukaMsj);
  }

  scrollToBottom() {
    this.scrollToBottomEvent.emit();
  }

  copyToClipboard(texto: string) {
    if(this.utils.isAndroid()){
      this.clipboard.copy(texto)
      .then(() => {
        this.utils.presentToast('Mensaje copiado', 'bottom', 'light')
      })
      .catch(err => {
        this.utils.presentToast('NO se ha copiado el mensaje', 'bottom', 'danger')
        console.error('Error al copiar al portapapeles: ' + JSON.stringify(err));
      });
    }
  }

}
