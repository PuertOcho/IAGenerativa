import { Component, Input, OnInit } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { TipoEvento } from 'src/app/models/nuka.model';

@Component({
  selector: 'app-evento-deportivo-modal',
  templateUrl: './evento-deportivo-modal.component.html',
  styleUrls: ['./evento-deportivo-modal.component.scss'],
})
export class EventoDeportivoModalComponent implements OnInit {

  @Input() eventoDeportivos: any;
  @Input() equipoLocal: any;
  @Input() equipoVisitante: any;

  constructor(private modalCtrl: ModalController) {}
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  closeModal() {
    this.modalCtrl.dismiss();
  }

  getIconoTipoEvento(evento: TipoEvento): string {
    switch (evento) {
      case TipoEvento.TARJETA_AMARILLA:
        return '/assets/icon/eventoDeportivo/TARJETA_AMARILLA.png';
      case TipoEvento.TARJETA_ROJA:
        return '/assets/icon/eventoDeportivo/TARJETA_ROJA.png';
      case TipoEvento.DOBLE_TARJETA_AMARILLA:
        return '/assets/icon/eventoDeportivo/DOBLE_TARJETA_AMARILLA.png';
      case TipoEvento.GOL:
        return '/assets/icon/eventoDeportivo/GOL.png';
      case TipoEvento.GOL_ANULADO:
        return '/assets/icon/eventoDeportivo/GOL_ANULADO.png';
      case TipoEvento.GOL_PENALTI:
        return '/assets/icon/eventoDeportivo/GOL_PENALTI.png';
      case TipoEvento.GOL_PROPIA:
        return '/assets/icon/eventoDeportivo/GOL_PROPIA.png';
      case TipoEvento.SUSTITUCION_IN:
        return '/assets/icon/eventoDeportivo/SUSTITUCION_IN.png';
      case TipoEvento.SUSTITUCION_OUT:
        return '/assets/icon/eventoDeportivo/SUSTITUCION_OUT.png';
      default:
        return 'help-circle-outline';
    }

  }
}
