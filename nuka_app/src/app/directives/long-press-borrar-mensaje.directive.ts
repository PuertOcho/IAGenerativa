import { Directive, Output, EventEmitter, HostListener } from '@angular/core';
import { EventsService } from '../services/events.service';

@Directive({
  selector: '[appLongPressBorrarMensaje]'
})
export class LongPressBorrarMensajeDirective {
  @Output('longPress') longPress: EventEmitter<any> = new EventEmitter();
  longPressTimeout: any;

  initialPosition: { x: number, y: number } | null = null;

  constructor(public eventsService: EventsService) {}

  @HostListener('touchstart', ['$event'])
  @HostListener('mousedown', ['$event'])
  onMouseDown(event: MouseEvent | TouchEvent) {

    this.longPressTimeout = setTimeout(() => {
      this.longPress.emit({ initialPosition: this.initialPosition});
      this.eventsService.publish('borrarMensajes', true);
    }, 2000);

    window.addEventListener('scroll', this.cancelLongPress, true);
    window.addEventListener('mousemove', this.onMouseMove, true);
  }

  @HostListener('touchend')
  @HostListener('mouseup')
  @HostListener('mouseleave')
  @HostListener('touchmove')
  onMouseUp() {
    this.cancelLongPress();
  }

  onMouseMove = (event: MouseEvent | TouchEvent) => {
    this.cancelLongPress();
  }


  cancelLongPress = () => {
    clearTimeout(this.longPressTimeout);
    window.removeEventListener('scroll', this.cancelLongPress, true);
    window.removeEventListener('mousemove', this.onMouseMove, true);
  }
    
}