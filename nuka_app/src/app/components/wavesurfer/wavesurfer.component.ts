import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { NukaChat, NukaMsj } from 'src/app/models/nuka.model';
import { DataManagementService } from 'src/app/services/data-management.service';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-wavesurfer',
  templateUrl: './wavesurfer.component.html',
  styleUrls: ['./wavesurfer.component.scss'],
})
export class WavesurferComponent implements OnInit {
  @Input() message: NukaMsj | undefined;

  public centesimas: number = 0;
  public minutos: number = 59;
  public segundos: number = 0;
  public contador: any;

  public _centesimas: string = '00';
  public _minutos: string = '00';
  public _segundos: string = '00';
  public waveformId: string = ''
  
  isRun = false;
  estado: string = 'pause';
  refreshColor = 'light';
  tiempo: number = 0;
  audio: any = null;
  audioListo: boolean = false;
  maxRange = 0;
  valueRange = 0;
  audioError: boolean = false;
  cargandoAudio: boolean = true;

  public currentTimeMilis: number | any;
  public duration: number | any;
  
  constructor(public utils: UtilsService, private dataManagementService: DataManagementService,) { 
  }

  public async init() {
      if (this.message?.audioFile) {
        await this.initLocalAudio();
      } else if (this.message?.needAudio) {
        await this.getResourceAudio();
      }
      this.cargandoAudio = false;
  }

  async getResourceAudio() {
    if (this.message && this.message.id && this.message.needAudio && this.message.audioResource) {
      const blob = await this.dataManagementService.getResource(this.message.id, this.message.audioResource);
      if (blob) {
        const audioUrl = URL.createObjectURL(blob);
        this.audio = new Audio(audioUrl);
        this.audioListo = true;
      } else {
        this.audioError = true;
      }
    } else {
      this.audioError = true;
    }
  }

  checkAudio() {
    return this.audio != null;
  }

  initLocalAudio() {
    console.log('initLocalAudio');
    this.waveformId = 'waveform_' + this.message?.id;
    const fileReader = new FileReader();
    fileReader.onload =  (event) => {
      const arrayBuffer = event?.target?.result as ArrayBuffer;
      const blob = new Blob([new Uint8Array(arrayBuffer)], { type: 'audio/webm' });
      const audioUrl = URL.createObjectURL(blob);
      this.audio = new Audio(audioUrl);
      this.audioListo = true;
    };
    fileReader.readAsArrayBuffer(this.message?.audioFile);
  }

  ngOnInit () {
    this.init();
  }

  play() {
    this.isRun = true;
    this.estado = 'play';
    this.audio.play();
    const idInteval = setInterval(() => {
      if (this.estado == 'pausa') {
        clearInterval(idInteval);
      }
      this.audio.addEventListener("ended", () => {
        console.log("chimpun la reproducción");
        this.audio.currentTime = 0;
      });
      if (this.audio.paused) {
        this.estado = 'pause';
        this.isRun = false;
        clearInterval(idInteval);
      } 

    }, 250)
  }

  pause() {
    this.audio.pause();
    this.isRun = false;
    this.estado = 'pause';
  }

  cambiarcurrentTimeMilis(event: any){
    if (!this.isRun && event.detail.value) {
      this.currentTimeMilis = event.detail.value;
      this.audio.currentTime = this.currentTimeMilis / 1000;
    }
  }

  getcurrentTimeMilis() {
    this.currentTimeMilis = Math.floor(this.audio.currentTime * 1000)
    return this.currentTimeMilis;
  }

  getDuration() {
    return Math.floor(this.audio.duration * 1000);
  }

  public formatSecondsToTime(miliseconds: number): string {
    const minutos = Math.floor((miliseconds / 1000) / 60);
    const segundos = Math.floor(miliseconds / 1000);
    const centesimas = Math.floor((miliseconds % 1000) / 10);
    this._minutos = minutos < 10 ? '0' + minutos : minutos.toString();
    this._segundos = segundos < 10 ? '0' + segundos : segundos.toString();
    this._centesimas = centesimas < 10 ? '0' + centesimas : centesimas > 99 ? Math.floor(centesimas / 10).toString(): centesimas.toString();
    const res = `${this._minutos}:${this._segundos}.${this._centesimas}`
    return res.includes('NaN') ? '--:--.--' : res;
  }

  start() {
    this.contador = setInterval(() => {
      this.centesimas += 1;
      if (this.centesimas < 10) this._centesimas = '0' + this.centesimas;
      else this._centesimas = '' + this.centesimas;
      if (this.centesimas == 10) {
        this.centesimas = 0;
        this.segundos += 1;
        if (this.segundos < 10) this._segundos = '0' + this.segundos;
        else this._segundos = this.segundos + '';
        if (this.segundos == 60) {
          this.segundos = 0;
          this.minutos += 1;
          if (this.minutos < 10) this._minutos = '0' + this.minutos;
          else this._minutos = this.minutos + '';
          this._segundos = '00';
          if (this.minutos == 90) {
            this.pause();
          }
        }
      }
    }, 100)
  }

  stop() {
    if (!this.isRun) {
      clearInterval(this.contador);
      this.estado = 'pause';
      this.isRun = false;
    }
  }

  estadoSwap() {
    this.isRun = !this.isRun;
    if (this.isRun) {
      this.estado = 'pause';
      this.refreshColor = 'gris';
      //this.start();
      this.play()
    } else {
      this.estado = 'play';
      this.refreshColor = 'light';
      this.pause();
    }

  }
}

