import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ContenidoMsj, NukaMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';
import { ImageModalComponent } from '../image-modal/image-modal.component';
import { ModalController } from '@ionic/angular';

@Component({
  selector: 'app-image-item',
  templateUrl: './image-item.component.html',
  styleUrls: ['./image-item.component.scss'],
})
export class ImageItemComponent implements OnInit {
  @Input() message: NukaMsj | undefined;
  imagenUrls: any[] = [];
  allImagesReady: boolean = false;
  loading: boolean = true;
  contenidoImagenes: ContenidoMsj[] = [];
  currentSlide = 0;
  numeros: number[] = [];

  constructor(
    public dataManagementService: DataManagementService,
    public utils: UtilsService,
    public modalController: ModalController) {
    }

  async ngOnInit() {
    if (this.message?.contenidoMsj) {
      this.contenidoImagenes = this.message?.contenidoMsj.filter(c => c.tipo?.includes('imagen'))
      this.numeros = Array.from({ length: this.contenidoImagenes.length }, (_, i) => i + 1);
      await this.contenidoImagenes.forEach(async ci => {
        await this.getResourceImagen(ci)
      })
      this.allImagesReady = true;
      this.loading = !this.allImagesReady;
    }
  }

  goToSlide(index: number) {
    this.currentSlide = index;
  }

  public checkImagenes() {
    return this.imagenUrls.length > 0 && this.allImagesReady;
  }

  async abrirImagenTamCompleto(imagenUrl: string) {
    const modal = await this.modalController.create({
      component: ImageModalComponent,
      componentProps: {
        imagenUrl: imagenUrl,
      },
      cssClass: 'fullscreen'
    });
    return await modal.present();
  }

  consoleLog() {
    console.log('left');
  }

  async getResourceImagen(contenidoImagen: ContenidoMsj) {
    if (contenidoImagen.id && contenidoImagen.tipo) {
      const blob = await this.dataManagementService.getResource(contenidoImagen.id, contenidoImagen.tipo);
      if (blob) {
        const imagenUrl = URL.createObjectURL(blob);
        this.imagenUrls.push(imagenUrl);
      }
    }
  }

}
