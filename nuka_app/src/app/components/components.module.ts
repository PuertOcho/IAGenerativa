import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { ChatItemComponent } from './chat-item/chat-item.component';
import { ChatGroupComponent } from './chat-group/chat-group.component';
import { PipesModule } from '../pipes/pipes.module';
import { ImageItemComponent } from './image-item/image-item.component';
import { OptionsItemComponent } from './options-item/options-item.component';
import { LoadingSkeletonItemComponent } from './loading-skeleton-item/loading-skeleton-item.component';
import { OptionModalComponent } from './option-modal/option-modal.component';
import { OptionsModalComponent } from './options-modal/options-modal.component';
import { BarraBusquedaItemComponent } from './barra-busqueda-item/barra-busqueda-item.component';
import { PaginacionItemComponent } from './paginacion-item/paginacion-item.component';
import { ImageModalComponent } from './image-modal/image-modal.component';
import { EstadoItemComponent } from './estado-item/estado-item.component';
import { OpcionesRapidasItemComponent } from './opciones-rapidas-item/opciones-rapidas-item.component';
import { AppDataModel } from '../app.data.model';
import { ActualizarChatComponent } from './actualizar-chat/actualizar-chat.component';
import { LongPressBorrarMensajeDirective } from '../directives/long-press-borrar-mensaje.directive';
import { InvertirSentidoScrollDirective } from '../directives/invertirSentidoScroll.directive';
import { LongPressModificarCalendarioDirective } from '../directives/long-press-modificar-calendario.directive';
import { ListOpcionesBasicasComponent } from './list-opciones-basicas/list-opciones-basicas.component';
import { EventoDeportivoModalComponent } from './evento-deportivo-modal/evento-deportivo-modal.component';
import { EstrategiaModalComponent } from './estrategia-modal/estrategia-modal.component';
import { FiltroNukaComponent } from './filtro-nuka/filtro-nuka.component';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { WavesurferComponent } from './wavesurfer/wavesurfer.component';

@NgModule({
  declarations: [
    ChatItemComponent,
    ChatGroupComponent,
    ImageItemComponent,
    OptionsItemComponent,
    LoadingSkeletonItemComponent,
    OptionModalComponent,
    OptionsModalComponent,
    BarraBusquedaItemComponent,
    PaginacionItemComponent,
    ImageModalComponent,
    EstadoItemComponent,
    OpcionesRapidasItemComponent,
    ActualizarChatComponent,
    LongPressBorrarMensajeDirective,
    LongPressModificarCalendarioDirective,
    InvertirSentidoScrollDirective,
    ListOpcionesBasicasComponent,
    EventoDeportivoModalComponent,
    EstrategiaModalComponent,
    FiltroNukaComponent,
    WavesurferComponent
  ],
  imports: [
    CommonModule,
    IonicModule,
    PipesModule,
    NgxGraphModule
  ],
  exports: [
    ChatItemComponent,
    ChatGroupComponent,
    ImageItemComponent,
    OptionsItemComponent,
    LoadingSkeletonItemComponent,
    OptionModalComponent,
    OptionsModalComponent,
    BarraBusquedaItemComponent,
    PaginacionItemComponent,
    ImageModalComponent,
    EstadoItemComponent,
    OpcionesRapidasItemComponent,
    ActualizarChatComponent,
    ListOpcionesBasicasComponent,
    EventoDeportivoModalComponent,
    EstrategiaModalComponent,
    FiltroNukaComponent,
    WavesurferComponent
  ]
})
export class ComponentsModule { }
