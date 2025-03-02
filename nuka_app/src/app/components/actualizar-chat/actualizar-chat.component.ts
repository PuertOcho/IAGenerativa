import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-actualizar-chat',
  templateUrl: './actualizar-chat.component.html',
  styleUrls: ['./actualizar-chat.component.scss'],
})
export class ActualizarChatComponent implements OnInit {

  constructor() { }

  ngOnInit() {}

  public sincronizarChat() {
    console.log('sincronizarChat');
  }

}
