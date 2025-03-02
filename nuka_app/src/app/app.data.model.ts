import { ConfigService } from './config/config.service';

export class AppDataModel {
  // Data keys
  public languages = 'languages';
  public token = 'token';
 
  // Interceptor headers
  public HEADER_AUTH = 'Authorization';
  public HEADER_VALUE_BEARER = 'Bearer ';

  public PATH_IMG_AJUSTES = 'assets/icon/';
  public PATH_IMG_AJUSTES_POR_DEFECTO = this.PATH_IMG_AJUSTES + 'add-image.gif';
  public IMG_INFO = [
    {
      id: 'DEFAULT',
      label: 'Pulse la imagen',
      name: 'add-image.gif' 
    },
    {
      id: '0000',
      label: 'Pulse la imagen',
      name: '0000.gif' 
    },
    {
      id: '0001',
      label: 'Pulse la imagen',
      name: '0001.gif' 
    },
    {
      id: '0002',
      label: 'Pulse la imagen',
      name: '0002.gif' 
    },
    {
      id: '0003',
      label: 'Pulse la imagen',
      name: '0003.gif' 
    },
    {
      id: '0004',
      label: 'Pulse la imagen',
      name: '0004.gif' 
    },
    {
      id: '0005',
      label: 'Pulse la imagen',
      name: '0005.gif' 
    },
    {
      id: '0006',
      label: 'Pulse la imagen',
      name: '0006.gif' 
    },
    {
      id: '0007',
      label: 'Pulse la imagen',
      name: '0007.gif' 
    },
    {
      id: '0008',
      label: 'Pulse la imagen',
      name: '0008.gif' 
    },
    {
      id: '0009',
      label: 'Pulse la imagen',
      name: '0009.gif' 
    },
    {
      id: '0010',
      label: 'Pulse la imagen',
      name: '0010.gif' 
    },
    {
      id: '0011',
      label: 'Pulse la imagen',
      name: '0011.png' 
    },
    {
      id: '0012',
      label: 'Pulse la imagen',
      name: '0012.png' 
    },
    {
      id: '0013',
      label: 'Pulse la imagen',
      name: '0013.png' 
    },
    {
      id: '0014',
      label: 'Pulse la imagen',
      name: '0014.png' 
    },
    {
      id: '0015',
      label: 'Pulse la imagen',
      name: '0015.png' 
    },
    {
      id: '0016',
      label: 'Pulse la imagen',
      name: '0016.png' 
    },
    {
      id: '0017',
      label: 'Pulse la imagen',
      name: '0017.png' 
    },
    {
      id: '0018',
      label: 'Pulse la imagen',
      name: '0018.png' 
    },
    {
      id: '0019',
      label: 'Pulse la imagen',
      name: '0019.png' 
    },
    {
      id: '0020',
      label: 'Pulse la imagen',
      name: '0020.png' 
    },
    {
      id: '0021',
      label: 'Pulse la imagen',
      name: '0021.png' 
    },
    {
      id: '0022',
      label: 'Pulse la imagen',
      name: '0022.png' 
    },
    {
      id: '0023',
      label: 'Pulse la imagen',
      name: '0023.png' 
    },
    {
      id: '0024',
      label: 'Pulse la imagen',
      name: '0024.png' 
    },
    {
      id: '0025',
      label: 'Pulse la imagen',
      name: '0025.png' 
    },
    {
      id: '0026',
      label: 'Pulse la imagen',
      name: '0026.png' 
    },
    {
      id: '0027',
      label: 'Pulse la imagen',
      name: '0027.png' 
    },
    {
      id: '0028',
      label: 'Pulse la imagen',
      name: '0028.png' 
    },
    {
      id: '0029',
      label: 'Pulse la imagen',
      name: '0029.png' 
    },
    {
      id: '0030',
      label: 'Pulse la imagen',
      name: '0030.png' 
    },
    {
      id: '0031',
      label: 'Pulse la imagen',
      name: '0031.png' 
    },
    {
      id: '0032',
      label: 'Pulse la imagen',
      name: '0032.png' 
    },
    {
      id: '0033',
      label: 'Pulse la imagen',
      name: '0033.png' 
    },
    {
      id: '0034',
      label: 'Pulse la imagen',
      name: '0034.png' 
    },
    {
      id: '0035',
      label: 'Pulse la imagen',
      name: '0035.png' 
    },
    {
      id: '0036',
      label: 'Pulse la imagen',
      name: '0036.png' 
    },
    {
      id: '0037',
      label: 'Pulse la imagen',
      name: '0037.png' 
    },
    {
      id: '0038',
      label: 'Pulse la imagen',
      name: '0038.png' 
    },
    {
      id: '0039',
      label: 'Pulse la imagen',
      name: '0039.png' 
    },
    {
      id: '0040',
      label: 'Pulse la imagen',
      name: '0040.png' 
    },
    {
      id: '0041',
      label: 'Pulse la imagen',
      name: '0041.png' 
    },
    {
      id: '0042',
      label: 'Pulse la imagen',
      name: '0042.png' 
    },
    {
      id: '0043',
      label: 'Pulse la imagen',
      name: '0043.png' 
    },
    {
      id: '0044',
      label: 'Pulse la imagen',
      name: '0044.png' 
    },
    {
      id: '0045',
      label: 'Pulse la imagen',
      name: '0045.png' 
    },
    {
      id: '0046',
      label: 'Pulse la imagen',
      name: '0046.png' 
    },
    {
      id: '0047',
      label: 'Pulse la imagen',
      name: '0047.png' 
    },
    {
      id: '0048',
      label: 'Pulse la imagen',
      name: '0048.png' 
    },
    {
      id: '0049',
      label: 'Pulse la imagen',
      name: '0049.png' 
    },
    {
      id: '0050',
      label: 'Pulse la imagen',
      name: '0050.png' 
    },
    {
      id: '0051',
      label: 'Pulse la imagen',
      name: '0051.png' 
    },
    {
      id: '0052',
      label: 'Pulse la imagen',
      name: '0052.png' 
    },
    {
      id: '0053',
      label: 'Pulse la imagen',
      name: '0053.png' 
    },
    {
      id: '0054',
      label: 'Pulse la imagen',
      name: '0054.png' 
    },
    {
      id: '0055',
      label: 'Pulse la imagen',
      name: '0055.png' 
    },
    {
      id: '0056',
      label: 'Pulse la imagen',
      name: '0056.png' 
    },
    {
      id: '0057',
      label: 'Pulse la imagen',
      name: '0057.png' 
    },
    {
      id: '0058',
      label: 'Pulse la imagen',
      name: '0058.png' 
    },
    {
      id: '0059',
      label: 'Pulse la imagen',
      name: '0059.png' 
    },
    {
      id: '0060',
      label: 'Pulse la imagen',
      name: '0060.png' 
    },
    {
      id: '0061',
      label: 'Pulse la imagen',
      name: '0061.png' 
    },
    {
      id: '0062',
      label: 'Pulse la imagen',
      name: '0062.png' 
    },
    {
      id: '0063',
      label: 'Pulse la imagen',
      name: '0063.png' 
    },
    {
      id: '0064',
      label: 'Pulse la imagen',
      name: '0064.png' 
    },
    {
      id: '0065',
      label: 'Pulse la imagen',
      name: '0065.png' 
    },
    {
      id: '0066',
      label: 'Pulse la imagen',
      name: '0066.png' 
    },
    {
      id: '0067',
      label: 'Pulse la imagen',
      name: '0067.png' 
    },
    {
      id: '0068',
      label: 'Pulse la imagen',
      name: '0068.png' 
    },
    {
      id: '0069',
      label: 'Pulse la imagen',
      name: '0069.png' 
    },
    {
      id: '0070',
      label: 'Pulse la imagen',
      name: '0070.png' 
    },
    {
      id: '0071',
      label: 'Pulse la imagen',
      name: '0071.png' 
    },
    {
      id: '0072',
      label: 'Pulse la imagen',
      name: '0072.png' 
    },
    {
      id: '0073',
      label: 'Pulse la imagen',
      name: '0073.png' 
    },
    {
      id: '0074',
      label: 'Pulse la imagen',
      name: '0074.png' 
    },
    {
      id: '0075',
      label: 'Pulse la imagen',
      name: '0075.png' 
    },
    {
      id: '0076',
      label: 'Pulse la imagen',
      name: '0076.png' 
    },
    {
      id: '0077',
      label: 'Pulse la imagen',
      name: '0077.png' 
    },
    {
      id: '0078',
      label: 'Pulse la imagen',
      name: '0078.png' 
    },
    {
      id: '0079',
      label: 'Pulse la imagen',
      name: '0079.png' 
    },
    {
      id: '0080',
      label: 'Pulse la imagen',
      name: '0080.png' 
    },
    {
      id: '0081',
      label: 'Pulse la imagen',
      name: '0081.png' 
    },
    {
      id: '0082',
      label: 'Pulse la imagen',
      name: '0082.png' 
    },
    {
      id: '0083',
      label: 'Pulse la imagen',
      name: '0083.png' 
    },
    {
      id: '0084',
      label: 'Pulse la imagen',
      name: '0084.png' 
    },
    {
      id: '0085',
      label: 'Pulse la imagen',
      name: '0085.png' 
    },
    {
      id: '0086',
      label: 'Pulse la imagen',
      name: '0086.png' 
    },
    {
      id: '0087',
      label: 'Pulse la imagen',
      name: '0087.png' 
    },
    {
      id: '0088',
      label: 'Pulse la imagen',
      name: '0088.png' 
    },
    {
      id: '0089',
      label: 'Pulse la imagen',
      name: '0089.png' 
    },
    {
      id: '0090',
      label: 'Pulse la imagen',
      name: '0090.png' 
    },
    {
      id: '0091',
      label: 'Pulse la imagen',
      name: '0091.png' 
    },
    {
      id: '0092',
      label: 'Pulse la imagen',
      name: '0092.png' 
    },
    {
      id: '0093',
      label: 'Pulse la imagen',
      name: '0093.png' 
    },
    {
      id: '0094',
      label: 'Pulse la imagen',
      name: '0094.png' 
    },
    {
      id: '0095',
      label: 'Pulse la imagen',
      name: '0095.png' 
    },
    {
      id: '0096',
      label: 'Pulse la imagen',
      name: '0096.png' 
    },
    {
      id: "0097",
      label: "Pulse la imagen",
      name: "0097.gif"
    },
    {
      id: "0098",
      label: "Pulse la imagen",
      name: "0098.gif"
    },
    {
      id: "0099",
      label: "Pulse la imagen",
      name: "0099.gif"
    },
    {
      id: "0100",
      label: "Pulse la imagen",
      name: "0100.gif"
    },
    {
      id: "0101",
      label: "Pulse la imagen",
      name: "0101.gif"
    },
    {
      id: "0102",
      label: "Pulse la imagen",
      name: "0102.gif"
    },
    {
      id: "0103",
      label: "Pulse la imagen",
      name: "0103.gif"
    },
    {
      id: "0104",
      label: "Pulse la imagen",
      name: "0104.gif"
    },
    {
      id: "0105",
      label: "Pulse la imagen",
      name: "0105.gif"
    },
    {
      id: "0106",
      label: "Pulse la imagen",
      name: "0106.gif"
    },
    {
      id: "0107",
      label: "Pulse la imagen",
      name: "0107.gif"
    },
    {
      id: "0108",
      label: "Pulse la imagen",
      name: "0108.gif"
    },
    {
      id: "0109",
      label: "Pulse la imagen",
      name: "0109.gif"
    },
    {
      id: "0110",
      label: "Pulse la imagen",
      name: "0110.gif"
    },
    {
      id: "0111",
      label: "Pulse la imagen",
      name: "0111.gif"
    },
    {
      id: "0112",
      label: "Pulse la imagen",
      name: "0112.gif"
    },
    {
      id: "0113",
      label: "Pulse la imagen",
      name: "0113.gif"
    },
    {
      id: "0114",
      label: "Pulse la imagen",
      name: "0114.gif"
    },
    {
      id: "0115",
      label: "Pulse la imagen",
      name: "0115.gif"
    },
    {
      id: "0116",
      label: "Pulse la imagen",
      name: "0116.gif"
    },
    {
      id: "0117",
      label: "Pulse la imagen",
      name: "0117.gif"
    },
    {
      id: "0118",
      label: "Pulse la imagen",
      name: "0118.gif"
    },
    {
      id: "0119",
      label: "Pulse la imagen",
      name: "0119.gif"
    },
    {
      id: "0120",
      label: "Pulse la imagen",
      name: "0120.gif"
    },
    {
      id: "0121",
      label: "Pulse la imagen",
      name: "0121.gif"
    },
    {
      id: "0122",
      label: "Pulse la imagen",
      name: "0122.gif"
    },
    {
      id: "0123",
      label: "Pulse la imagen",
      name: "0123.gif"
    },
    {
      id: "0124",
      label: "Pulse la imagen",
      name: "0124.gif"
    },
    {
      id: "0125",
      label: "Pulse la imagen",
      name: "0125.gif"
    },
    {
      id: "0126",
      label: "Pulse la imagen",
      name: "0126.gif"
    },
    {
      id: "0127",
      label: "Pulse la imagen",
      name: "0127.gif"
    },
    {
      id: "0128",
      label: "Pulse la imagen",
      name: "0128.gif"
    },
    {
      id: "0129",
      label: "Pulse la imagen",
      name: "0129.gif"
    },
    {
      id: "0130",
      label: "Pulse la imagen",
      name: "0130.gif"
    },
    {
      id: "0131",
      label: "Pulse la imagen",
      name: "0131.gif"
    },
    {
      id: "0132",
      label: "Pulse la imagen",
      name: "0132.gif"
    },
    {
      id: "0133",
      label: "Pulse la imagen",
      name: "0133.gif"
    },
    {
      id: "0134",
      label: "Pulse la imagen",
      name: "0134.gif"
    },
    {
      id: "0135",
      label: "Pulse la imagen",
      name: "0135.gif"
    },
    {
      id: "0136",
      label: "Pulse la imagen",
      name: "0136.gif"
    },
    {
      id: "0137",
      label: "Pulse la imagen",
      name: "0137.gif"
    },
    {
      id: "0138",
      label: "Pulse la imagen",
      name: "0138.gif"
    },
    {
      id: "0139",
      label: "Pulse la imagen",
      name: "0139.gif"
    },
    {
      id: "0140",
      label: "Pulse la imagen",
      name: "0140.gif"
    },
    {
      id: "0141",
      label: "Pulse la imagen",
      name: "0141.gif"
    },
    {
      id: "0142",
      label: "Pulse la imagen",
      name: "0142.gif"
    },
    {
      id: "0143",
      label: "Pulse la imagen",
      name: "0143.gif"
    },
    {
      id: "0144",
      label: "Pulse la imagen",
      name: "0144.gif"
    },
    {
      id: "0145",
      label: "Pulse la imagen",
      name: "0145.gif"
    },
    {
      id: "0146",
      label: "Pulse la imagen",
      name: "0146.gif"
    },
    {
      id: "0147",
      label: "Pulse la imagen",
      name: "0147.gif"
    },
    {
      id: "0148",
      label: "Pulse la imagen",
      name: "0148.gif"
    },
    {
      id: "0149",
      label: "Pulse la imagen",
      name: "0149.gif"
    },
    {
      id: "0150",
      label: "Pulse la imagen",
      name: "0150.gif"
    },
    {
      id: "0151",
      label: "Pulse la imagen",
      name: "0151.gif"
    },
    {
      id: "0152",
      label: "Pulse la imagen",
      name: "0152.gif"
    },
    {
      id: "0153",
      label: "Pulse la imagen",
      name: "0153.gif"
    },
    {
      id: "0154",
      label: "Pulse la imagen",
      name: "0154.gif"
    }
  ]

  public redirecciones = new Map<string, any>();
  
  constructor() { 
    this.redirecciones.set("shopping-list", 
    {
      etiqueta: 'Lista de la compra',
      redireccion: 'shopping-tabs/shopping-list',
      segmento: null
    });

    this.redirecciones.set("status", 
    {
      etiqueta: 'Pestaña de estado',
      redireccion: 'home-tabs/status',
      segmento: null
    });

    this.redirecciones.set("status:NOTIFICACIONES", 
    {
      etiqueta: 'Notificaciones',
      redireccion: 'home-tabs/status',
      segmento: 'NOTIFICACIONES'
    });

    this.redirecciones.set("status:CALENDARIO", 
    {
      etiqueta: 'Calendario',
      redireccion: 'home-tabs/status',
      segmento: 'CALENDARIO'
    });

    this.redirecciones.set("status:TAREAS", 
    {
      etiqueta: 'Tareas',
      redireccion: 'home-tabs/status',
      segmento: 'TAREAS'
    });

  }

}
