import { Component, Input, OnInit } from '@angular/core';
import { Apuesta, Bets, Estrategia } from 'src/app/models/nuka.model';

@Component({
  selector: 'app-estrategia-modal',
  templateUrl: './estrategia-modal.component.html',
  styleUrls: ['./estrategia-modal.component.scss'],
})
export class EstrategiaModalComponent implements OnInit {

  @Input() estrategia: Estrategia | any;
  @Input() apuestas: Apuesta[] | any;
  @Input() encuentros: Bets[] | any;

  isListVisible: boolean = false;
  loading: boolean = false;

  constructor() { }

  ngOnInit() {}

  getEncuentro(idEncuentro: string): Bets {
    return this.encuentros.find((e: { id: string; }) => e.id == idEncuentro)
  }
}
