import { Directive, HostListener, ElementRef } from '@angular/core';
import { IonContent } from '@ionic/angular';

@Directive({
  selector: '[appInvertirSentidoScroll]'
})
export class InvertirSentidoScrollDirective {

  private ionContentElement: IonContent | any;

  constructor(private el: ElementRef) {}

  // Detectar el scroll y manejar el desplazamiento
  @HostListener('wheel', ['$event'])
  onScroll(event: WheelEvent) {
    // Prevenir el comportamiento predeterminado
    event.preventDefault();

    // Obtener el ion-content más cercano
    this.ionContentElement = this.el.nativeElement.closest('ion-content');

    // Verificar si el contenedor ion-content fue encontrado
    if (this.ionContentElement) {
      // Invertir el scroll usando scrollByPoint
      if (event.deltaY > 0) {
        // Si el usuario desplaza hacia abajo, desplazamos hacia arriba
        this.ionContentElement.scrollByPoint(0, -Math.abs(event.deltaY), 0);
      } else {
        // Si el usuario desplaza hacia arriba, desplazamos hacia abajo
        this.ionContentElement.scrollByPoint(0, Math.abs(event.deltaY), 0);
      }
    }
  }
}
