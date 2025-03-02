import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigService } from '../config/config.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {

  wsUrl: string | any;

  constructor(
    public config: ConfigService
  ) {
    this.wsUrl = this.config.config().wsUrl;
  }

  conectWS(endpoint: string): WebSocketSubject<any> {
    const socket = webSocket(this.wsUrl + endpoint);
    return socket;
  }

  sendMessage(socket: WebSocketSubject<any>, message: string): void {
    let res: any = {
        payload: message
    }
    socket.next(res);
  }

  onMessage(socket: WebSocketSubject<any>) {
    return socket.asObservable();
  }

  closeConnection(socket: WebSocketSubject<any>): void {
    socket.complete();
  }
}
