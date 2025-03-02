import { Directive, Output, EventEmitter, HostListener, Input } from '@angular/core';
import { EventsService } from '../services/events.service';

@Directive({
  selector: '[appLongPressModificarCalendario]'
})
export class LongPressModificarCalendarioDirective {
  @Input() id: string | any = '';
  @Output('longPress') longPress: EventEmitter<any> = new EventEmitter();
  longPressTimeout: any;

  constructor(
    public eventsService: EventsService
    ) {}

  @HostListener('touchstart', ['$event'])
  @HostListener('mousedown', ['$event'])
  onMouseDown(event: any) {
    this.longPressTimeout = setTimeout(() => {
      this.longPress.emit(event);
      this.eventsService.publish('modificarItemCalendario', { modificar: true, id: this.id});
    }, 2000);
  }

  @HostListener('touchend')
  @HostListener('mouseup')
  @HostListener('mouseleave')
  onMouseUp() {
    clearTimeout(this.longPressTimeout);
  }
}
