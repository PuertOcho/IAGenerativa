export class NukaMsj {
    id!: string;
    fecha: string | undefined;
    texto: string | undefined;
    audioFile: any | undefined;
    needAudio: boolean | undefined;
    audioResource: string | undefined;
    usuarioId: string | undefined;
    chatId: string | undefined;
    contenidoMsj: ContenidoMsj[] | undefined;
    tipoMsj: string | undefined; // PETICION, RESPUESTA
    tarea: string | undefined;
    redireccion: string | undefined;
    respondido: boolean | undefined;
    seleccionado: boolean | undefined;
}

export class EstadoMsj {
    id!: string;
    fecha: string | undefined;
    texto: string | undefined;
    usuarioId: string | undefined;
    tipoMsjEstado: string | undefined; // NOTIFICACIONES, CALENDARIO, TAREAS
    estado: string | undefined;
    imgMsjPath: string | undefined;
    redireccion: string | undefined;
    tiempoRepeticion: string | undefined;

    constructor() {
    }
}

export class User {
    id: string | undefined;
    userNick: string | undefined;
    username: string | undefined;
    password: string | undefined;
    email: string | undefined;
    phone: string | undefined;
    permission: string | undefined;
    registrationDay: string | undefined;
    preferences: Map<string, string> | undefined;
    opcionesModal: OpcionesModal | undefined;
    data: DataUser | undefined;
    activityHistory: string | undefined;
    lastAccessDate: string | undefined;
}


export enum TipoEstrategia {
    X = "X",
    UNO_DOS = "UNO_DOS",
    UNO = "UNO",
    DOS = "DOS",
    UNO_X = "UNO_X",
    X_DOS = "X_DOS",
    MAS_15 = "MAS_15",
    MENOS_15 = "MENOS_15",
    MAS_25 = "MAS_25",
    MENOS_25 = "MENOS_25",
    MAS_35 = "MAS_35",
    MENOS_35 = "MENOS_35",
    HT_UNO = "HT_UNO",
    HT_X = "HT_X",
    HT_DOS = "HT_DOS",
    PRIMER_GOL_DEL_NO_FAVORITO = "PRIMER_GOL_DEL_NO_FAVORITO",
    RACHA_DE_PARTIDOS = "RACHA_DE_PARTIDOS",
    RACHA_CORNERS = "RACHA_CORNERS",
    RACHA_TARJETAS = "RACHA_TARJETAS"
}

export enum EstadoApuesta {
    PENDIENTE = "PENDIENTE",
    EN_CURSO = "EN_CURSO",
    ACERTADA = "ACERTADA",
    FALLADA = "FALLADA",
    CANCELADA = "CANCELADA"
}

export enum CasaApuesta {
    BET365 = "BET365",
    CODERE = "CODERE",
    SPORT888 = "SPORT888",
    BWIN = "BWIN",
    BETFAIR = "BETFAIR",
    LEOVEGAS = "LEOVEGAS",
    RETABET = "RETABET",
    POKERSTARS = "POKERSTARS",
    SPORTIUM = "SPORTIUM",
    VERSUS = "VERSUS",
    WILLIAMHILL = "WILLIAMHILL",
    DAZNBET = "DAZNBET",
    BETWAY = "BETWAY",
    WINAMAX = "WINAMAX",
    KIROLBET = "KIROLBET"
}

export enum TipoGestor {
    AJUSTES = "AJUSTES",
    CASA_APUESTAS = "CASA_APUESTAS"
}

export abstract class GestorBase {
    id?: string;
    idUsuario?: string;
    tipo?: TipoGestor;

    constructor(idUsuario?: string, tipo?: TipoGestor) {
        this.idUsuario = idUsuario;
        this.tipo = tipo;
    }
}

/*
    * LIBRE: Se pueden añadir nuevas apuestas
    * PENDIENTE: Hay una apuesta pendiente de verificar
    * FINALIZADO: Se ha concluido la Estrategias (Cadena de apuestas)
*/
export enum EstadoEstrategia {
    LIBRE = "LIBRE",
    PENDIENTE = 'PENDIENTE',
    FINALIZADO = 'FINALIZADO'
}

export class GestorCasaApuesta extends GestorBase {
    casa?: CasaApuesta;
    url?: string;
    fecha?: number;
    saldo: number = 20;

    constructor(casa: CasaApuesta, fecha: number, idUsuario?: string) {
        super(idUsuario, TipoGestor.CASA_APUESTAS);
        this.casa = casa;
        this.fecha = fecha;
        this.url = GestorCasaApuesta.getUrlCasaApuestas(casa);
    }

    static getUrlCasaApuestas(casa: CasaApuesta): string | any {
        switch (casa) {
            case CasaApuesta.BET365:
                return "https://www.bet365.es/#/HO/";
            case CasaApuesta.CODERE:
                return "https://www.codere.es/";
            case CasaApuesta.SPORT888:
                return "https://www.888sport.es/";
            case CasaApuesta.BWIN:
                return "https://sports.bwin.es/es/sports";
            case CasaApuesta.BETFAIR:
                return "https://www.betfair.es/";
            case CasaApuesta.LEOVEGAS:
                return "https://www.leovegas.es/es-es/";
            case CasaApuesta.RETABET:
                return "https://andalucia.retabet.es/";
            case CasaApuesta.POKERSTARS:
                return "https://www.pokerstars.es/?&no_redirect=1";
            case CasaApuesta.SPORTIUM:
                return "https://www.sportium.es/apuestas";
            case CasaApuesta.VERSUS:
                return "https://www.versus.es/home";
            case CasaApuesta.WILLIAMHILL:
                return "https://www.williamhill.es/";
            case CasaApuesta.BETWAY:
                return "https://betway.es/";
            case CasaApuesta.DAZNBET:
                return "https://www.daznbet.es/es-es";
            case CasaApuesta.WINAMAX:
                return "https://www.winamax.es/";
            case CasaApuesta.KIROLBET:
                return "https://kirolbet.es/";
            default:
                return null;
        }
    }


}

export class Estrategia {
    id?: string;
    idUsuario?: string;
    fechaInicio?: number;
    tipo?: TipoEstrategia;
    idsApuestas?: string[];
    estado?: EstadoEstrategia;
    condicion?: string;
    estrategiaEnDirecto?: boolean;
    multiplicador?: Map<number, number>;

    constructor(
        id?: string,
        idUsuario?: string,
        fechaInicio?: number,
        tipo?: TipoEstrategia,
        idsApuestas?: string[],
        estado?: EstadoEstrategia,
        condicion?: string,
        estrategiaEnDirecto?: boolean
    ) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.fechaInicio = fechaInicio;
        this.tipo = tipo;
        this.idsApuestas = idsApuestas;
        this.estado = estado;
        this.condicion = condicion;
        this.estrategiaEnDirecto = estrategiaEnDirecto;
        this.multiplicador = new Map<number, number>();
    }
}

export class Apuesta {
    id?: string;
    idUsuario?: string;
    idEncuentro?: string;
    idEstrategia?: string;
    tipoEstrategia?: TipoEstrategia;
    fecha?: number;
    casa?: CasaApuesta;
    saldoDisponible?: number;
    cuota?: number;
    cantidad?: number;
    estadoApuesta?: EstadoApuesta;

    constructor(
        id?: string,
        idUsuario?: string,
        idEncuentro?: string,
        idEstrategia?: string,
        fecha?: number,
        cantidad?: number,
        estadoApuesta?: EstadoApuesta,
        tipoEstrategia?: TipoEstrategia,
        cuota?: number,
        casa?: CasaApuesta
    ) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idEncuentro = idEncuentro;
        this.idEstrategia = idEstrategia;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.estadoApuesta = estadoApuesta;
        this.tipoEstrategia = tipoEstrategia;
        this.cuota = cuota;
        this.casa = casa;
    }

    // Métodos getters y setters opcionales en TypeScript
    getId(): string | undefined {
        return this.id;
    }

    setId(id: string): void {
        this.id = id;
    }

    getIdUsuario(): string | undefined {
        return this.idUsuario;
    }

    setIdUsuario(idUsuario: string): void {
        this.idUsuario = idUsuario;
    }

    getIdEncuentro(): string | undefined {
        return this.idEncuentro;
    }

    setIdEncuentro(idEncuentro: string): void {
        this.idEncuentro = idEncuentro;
    }

    getIdEstrategia(): string | undefined {
        return this.idEstrategia;
    }

    setIdEstrategia(idEstrategia: string): void {
        this.idEstrategia = idEstrategia;
    }

    getFecha(): number | undefined {
        return this.fecha;
    }

    setFecha(fecha: number): void {
        this.fecha = fecha;
    }

    getCuota(): number | undefined {
        return this.cuota;
    }

    setCuota(cuota: number): void {
        this.cuota = cuota;
    }

    getCantidad(): number | undefined {
        return this.cantidad;
    }

    setCantidad(cantidad: number): void {
        this.cantidad = cantidad;
    }

    getEstadoApuesta(): EstadoApuesta | undefined {
        return this.estadoApuesta;
    }

    setEstadoApuesta(estadoApuesta: EstadoApuesta): void {
        this.estadoApuesta = estadoApuesta;
    }

    getCasa(): CasaApuesta | undefined {
        return this.casa;
    }

    setCasa(casa: CasaApuesta): void {
        this.casa = casa;
    }
}

export class Bets {
    id: string | undefined;
    deporte: string | undefined;
    liga: string | undefined;
    fecha: number | undefined;
    idEncuentro: string | undefined;
    idEncuentroEnDirecto: string | undefined;

    equipoLocal: string | undefined;
    equipoVisitante: string | undefined;

    goles1erTiempoLocal: number | undefined;
    goles1erTiempoVisitante: number | undefined;
    golesTotalesLocal: number | undefined;
    golesTotalesVisitante: number | undefined;

    prediccionStatarea: string | undefined;

    prob_1: number | undefined;
    prob_X: number | undefined;
    prob_2: number | undefined;
    prob_HT1: number | undefined;
    prob_HTX: number | undefined;
    prob_HT2: number | undefined;
    prob_15: number | undefined;
    prob_25: number | undefined;
    prob_35: number | undefined;
    prob_BTS: number | undefined;
    prob_OTS: number | undefined;

    cuota_1: number | undefined;
    cuota_X: number | undefined;
    cuota_2: number | undefined;
    cuota_HT1: number | undefined;
    cuota_HTX: number | undefined;
    cuota_HT2: number | undefined;
    cuota_mas_15: number | undefined;
    cuota_menos_15: number | undefined;
    cuota_mas_25: number | undefined;
    cuota_menos_25: number | undefined;
    cuota_mas_35: number | undefined;
    cuota_menos_35: number | undefined;
    cuota_BTS: number | undefined;
    cuota_OTS: number | undefined;

    avg_local_GF: number | undefined;
    avg_local_GC: number | undefined;
    avg_visitante_GF: number | undefined;
    avg_visitante_GC: number | undefined;

    url_comparacion: string | undefined;
    estado: string | undefined;
    url_resumen_finalizado: string | undefined;

    resumenDirecto: EventoDeportivo[] | undefined;
    resumenFinalizado: EventoDeportivo[] | undefined;
    ForosInformacion: ForoInformacion[] | undefined;

    constructor(init?: Partial<Bets>) {
        Object.assign(this, init);
    }

    getResultado(): string {
        if (this.golesTotalesLocal !== undefined && this.golesTotalesVisitante !== undefined) {
            return `${this.golesTotalesLocal}:${this.golesTotalesVisitante}`;
        }
        return '?:?';
    }

    // Method similar to `encuentroTerminado` in Java
    encuentroTerminado(): boolean | any {
        return (
            this.golesTotalesLocal != null || 
            this.golesTotalesVisitante != null || 
            this.estado != null || 
            this.url_resumen_finalizado != null || 
            (this.resumenFinalizado && this.resumenFinalizado.length > 0)
        );
    }

    toString(): string {
        return `Bets [id=${this.id}, deporte=${this.deporte}, liga=${this.liga}, fecha=${this.fecha}, 
                idEncuentro=${this.idEncuentro}, idEncuentroEnDirecto=${this.idEncuentroEnDirecto}, 
                equipoLocal=${this.equipoLocal}, equipoVisitante=${this.equipoVisitante}, 
                goles1erTiempoLocal=${this.goles1erTiempoLocal}, goles1erTiempoVisitante=${this.goles1erTiempoVisitante}, 
                golesTotalesLocal=${this.golesTotalesLocal}, golesTotalesVisitante=${this.golesTotalesVisitante}, 
                prediccionStatarea=${this.prediccionStatarea}, prob_1=${this.prob_1}, prob_X=${this.prob_X}, 
                prob_2=${this.prob_2}, prob_HT1=${this.prob_HT1}, prob_HTX=${this.prob_HTX}, prob_HT2=${this.prob_HT2}, 
                prob_15=${this.prob_15}, prob_25=${this.prob_25}, prob_35=${this.prob_35}, prob_BTS=${this.prob_BTS}, 
                prob_OTS=${this.prob_OTS}, cuota_1=${this.cuota_1}, cuota_X=${this.cuota_X}, cuota_2=${this.cuota_2}, 
                cuota_HT1=${this.cuota_HT1}, cuota_HTX=${this.cuota_HTX}, cuota_HT2=${this.cuota_HT2}, 
                cuota_mas_15=${this.cuota_mas_15}, cuota_menos_15=${this.cuota_menos_15}, 
                cuota_mas_25=${this.cuota_mas_25}, cuota_menos_25=${this.cuota_menos_25}, 
                cuota_mas_35=${this.cuota_mas_35}, cuota_menos_35=${this.cuota_menos_35}, 
                cuota_BTS=${this.cuota_BTS}, cuota_OTS=${this.cuota_OTS}, avg_local_GF=${this.avg_local_GF}, 
                avg_local_GC=${this.avg_local_GC}, avg_visitante_GF=${this.avg_visitante_GF}, 
                avg_visitante_GC=${this.avg_visitante_GC}, url_comparacion=${this.url_comparacion}, 
                estado=${this.estado}, url_resumen_finalizado=${this.url_resumen_finalizado}]`;
    }
}

export class EventoDeportivo {
    minuto: string | undefined;
    evento: TipoEvento | undefined;
    jugador: string | undefined;
    equipo: LocalOVisitante | undefined;
    estadistica: string | undefined;

    constructor(minuto?: string, evento?: TipoEvento, jugador?: string, equipo?: LocalOVisitante, estadistica?: string) {
        this.minuto = minuto;
        this.evento = evento;
        this.jugador = jugador;
        this.equipo = equipo;
        this.estadistica = estadistica;
    }

    isNotEmpty(): boolean {
        return this.minuto !== undefined && this.evento !== undefined && this.jugador !== undefined && this.equipo !== undefined;
    }

    estadisticaIsNotEmpty(): boolean {
        return this.evento !== undefined && this.equipo !== undefined && this.estadistica !== undefined;
    }
}

export enum TipoEvento {
    TARJETA_AMARILLA = "TARJETA_AMARILLA",
    TARJETA_ROJA = "TARJETA_ROJA",
    DOBLE_TARJETA_AMARILLA = "DOBLE_TARJETA_AMARILLA",
    GOL = "GOL",
    GOL_ANULADO = "GOL_ANULADO",
    GOL_PENALTI = "GOL_PENALTI",
    GOL_PROPIA = "GOL_PROPIA",
    SUSTITUCION_IN = "SUSTITUCION_IN",
    SUSTITUCION_OUT = "SUSTITUCION_OUT",
    NUMERO_TARJETAS_AMARILLAS = "NUMERO_TARJETAS_AMARILLAS",
    NUMERO_TARJETAS_ROJAS = "NUMERO_TARJETAS_ROJAS",
    INTENTOS_TOTALES = "INTENTOS_TOTALES",
    INTENTOS_A_PUERTA = "INTENTOS_A_PUERTA",
    INTENTOS_FUERA = "INTENTOS_FUERA",
    CORNERS = "CORNERS",
    POSESION = "POSESION",
    FALTAS_COMETIDAS = "FALTAS_COMETIDAS",
    FUERA_DE_JUEGO = "FUERA_DE_JUEGO",
    GOLES_SALVADOS = "GOLES_SALVADOS"
}

export enum LocalOVisitante {
    LOCAL = "LOCAL",
    VISITANTE = "VISITANTE"
}

export class ForoInformacion {
    id: string | undefined;
    nombre: string | undefined;
    enlace: string | undefined;
    informacion: string | undefined;
}

export class LoginResponse {
    usuario!: User;
    token!: string;
}

export class DataUser {
    fechaUltimaActualizacion: string | undefined;
}

export class NukaChat {
    id: string | undefined;
    usuarioId: string | undefined;
    visible: boolean | undefined;
    favorito: boolean | undefined;
    tags: string[] | undefined;
    summary: string | undefined;
    fechaCreacion: string | undefined;
    fechaUltimoMsj: string | undefined;
    opcionesModal: OpcionesModal | undefined;
    arbolDocumentos: ArbolDocumentos | undefined;
    ultimaPosicionDeChat: number | undefined;
    numeroMensajes: number | undefined;
}

export class ArbolDocumentos {
    usarArbolDocumentos: boolean | undefined;
    idsDocumentos: string[] | undefined;
}

export class ContenidoMsj {
    id: string | undefined;
    tipo: string | undefined;
}

export class OpcionesMsj {
    etiqueta: string | undefined;
    opciones: OpcionMsj[] | undefined;
}

export class OpcionMsj {
    etiqueta: string | undefined;
    accion: OpcionAccionMsj | undefined;
    id: string | undefined;
}

export class OpcionAccionMsj {
    endpoint: string | undefined;
    datos: Map<string, string> | undefined;
    necesitaDatos: boolean | undefined;
    datosNecesarios: Map<string, OpcionAccionDatosMsj> | undefined;
}

export class OpcionAccionDatosMsj {
    etiqueta: string | undefined;
    tipo: string | undefined;
    auxiliar: string[] | undefined;
}

export class ShoppingList {
    id: string | undefined;
    isFinished: boolean | undefined;
    productRelations: ProductRelation[] | undefined;
    datePurchase: string | undefined;
}

export class ProductRelation {
    quantity: number | undefined;
    marcado: boolean | undefined;
    productInfo: Product | undefined;
}

export class Product {
    id: string | undefined;
    nombreProducto: string | undefined;
    aliasProducto: string[] | undefined;
    categoria: string | undefined;
    addedByUserId: string | undefined;
    embedding: number[] | undefined;
}

export class OpcionesModal {
    opciones: GrupoOpciones[] | undefined;
}

export class GrupoOpciones {
    etiqueta: string | undefined;
    opciones: OpcionBasica[] | undefined;
}

export class OpcionBasica {
    id: string | undefined;
    etiqueta: string | undefined;
    tipo: string | undefined;
    datos: string[] | undefined;
    valor: string[] | undefined;
}

export class posicionScroll {
    numItem: number | undefined;
    pos: number | undefined;
}

export class Filtro {
    id: string | undefined;
    datos: string[] | undefined;
    valores: string[] | undefined;

    constructor(id: string, datos: string[], valores: string[]) {
        this.id = id;
        this.datos = datos;
        this.valores = valores;
    }
}

