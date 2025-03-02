import { Component, Input, OnInit } from '@angular/core';
import { ContenidoMsj, NukaMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';

@Component({
  selector: 'app-loading-skeleton-item',
  templateUrl: './loading-skeleton-item.component.html',
  styleUrls: ['./loading-skeleton-item.component.scss'],
})
export class LoadingSkeletonItemComponent implements OnInit {

  @Input() numeroElementos!: string;
  @Input() formato!: string;

  bloques: any[] = [];
  elementos: any[] = []
  constructor(public dataManagementService: DataManagementService) { }

  assinarEstilo(item: string | null) {
    let CssStyles = null;
    if (item && item == 'circular') {
      CssStyles = {        
        '--border-radius': '9999px',
        // '--background': 'rgba(188, 0, 255, 0.065)',
        // '--background-rgb': '188, 0, 255'
      };
    }

    return CssStyles;
  }

  ngOnInit() {
    // la suma de todos los huecos tiene que ser 12
    // 1!(tamaño del hueco): n?(nada) c?(bordes circulares) r?(bordes rectos) 
    // ex: "2:n 10:c"    [  (==========)]
    // ex: "10:r 2:n"    [[==========]  ]
    // ex: "8:c 2:n 2:r" [(========)  [==]]
    this.elementos = Array.from(Array(Number(this.numeroElementos)).keys());
    let bloquesAux = this.formato.split(' ');
    bloquesAux.forEach((e: string) => {
      this.bloques.push(
        {
          espacio: e && e.includes(':') ? e.split(':')[0]: null,
          vacio: e && e.includes('n'),
          borde: e && e.includes('c') ? 'circular': e.includes('r') ? 'recto': 'circular'
        }
      );
    });
  }

}
