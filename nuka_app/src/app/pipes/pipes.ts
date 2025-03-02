import { Pipe, PipeTransform } from '@angular/core';
import { DateTime } from 'luxon';
import { UtilsService } from '../services/utils.service';

@Pipe({
  name: 'formatNumber'
})
export class FormatNumberPipe implements PipeTransform {
  transform(value: number | string, selectedCulture: any): any {
    if (value === 0) {
      return 0;
    }
    if (!value) {
      return;
    }
    if (selectedCulture) {
      return new Intl.NumberFormat(selectedCulture.replace('_', '-'), {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }).format(Number(value));
    } else {
      return new Intl.NumberFormat().format(Number(value));
    }
  }
}

import * as marked from 'marked';

@Pipe({name: 'markdown'})
export class MarkdownPipe implements PipeTransform {
  async transform(markdown: string): Promise<string> {
    //console.log(await marked.parse(markdown));
    return await marked.parse(markdown);
  }
}

@Pipe({
  name: 'formatSiNoDeBoolean'
})
export class FormatSiNoDeBoolean implements PipeTransform {
  transform(value: boolean | string | null): any {
    console.log('value', value)
    return value && (value == 'true' || value == 'True')? 'Si': 'No';
  }
}

@Pipe({
  name: 'formatFecha'
})
export class FormatFechaPipe implements PipeTransform {
  transform(fecha: string, format: string): string {
    const [datePart, timePart] = fecha.split('_');
    const [year, month, day] = datePart.split('-');
    const [hour, minute, second] = timePart.split('-');
    let formattedDate = format.replace('AAAA', year);
    formattedDate = formattedDate.replace('AA', year.slice(2,4));
    formattedDate = formattedDate.replace('MM', month);
    formattedDate = formattedDate.replace('DD', day);
    formattedDate = formattedDate.replace('hh', hour);
    formattedDate = formattedDate.replace('mm', minute);
    formattedDate = formattedDate.replace('ss', minute);
    return formattedDate;
  }
}

@Pipe({
  name: 'formatTimestampFecha'
})
export class FormatTimestampFecha implements PipeTransform {
    constructor(
      public utils: UtilsService
      ){}

  transform(timestamp: string | number | undefined, format: string): string {
    let fecha;
    if (typeof(timestamp) == 'number' ) {
      fecha = new Date(timestamp);
    } else if (typeof(timestamp) == 'string') {
      fecha = new Date(Number(timestamp));
    }
    
    if (fecha) {
      let formattedDate = format.replace('AAAA', fecha.getUTCFullYear().toString());
      formattedDate = formattedDate.replace('AA', fecha.getUTCFullYear().toString().slice(2,4));
  
      if (formattedDate.indexOf('MMMMM') > 0) {
        let nombreMes: any =this.utils.obtenerNombreMes(fecha.getMonth());
        formattedDate = formattedDate.replace('MMMMM', nombreMes);
      }
  
      formattedDate = formattedDate.replace('MM', this.añadirCero(fecha.getMonth() + 1, 'Month'));
      formattedDate = formattedDate.replace('DD', this.añadirCero(fecha.getDate(), 'Day'));
      formattedDate = formattedDate.replace('hh', this.añadirCero(fecha.getHours(), 'Hours'));
      formattedDate = formattedDate.replace('mm', this.añadirCero(fecha.getMinutes(), 'Minutes'));
      formattedDate = formattedDate.replace('ss', this.añadirCero(fecha.getSeconds(), 'Seconds'));
  
      //const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', hour12: false };
      //const formatter = new Intl.DateTimeFormat('en-US', options as any);
      //const dateString = formatter.format(fecha);
      return formattedDate;
    }
    
    return 'ERROR FORMATO FECHA';
  }

  añadirCero(date: number, tipo: string): string {
    if (tipo == 'Month') {
      if (date < 0) {
        return String('00');
      } else if (date < 10) {
        return String('0' + date);
      } else {
        return String(date);
      }
    } else {
      if (date < 10) {
        return String('0' + date);
      } else {
        return String(date);
      }
    }
  }
}

@Pipe({
  name: 'formatAudio'
})
export class FormatAudio implements PipeTransform {
  transform(duracion: number): string {
    if (duracion) {
      let format = 'mm:ss';
      const ss = Math.ceil(duracion / 1000) ;
      const mm = ss / 60;
      let formattedDate = format.replace('ss', () => {
        let seconds =  String(ss % 60);
        return seconds.length == 1 ? '0' + seconds: seconds;
      });
      formattedDate = formattedDate.replace('mm', mm > 1? String(Math.floor(mm)): '00');
      return formattedDate
    }
    return '00:00';
  }

  añadirCero(date: string) {
    if (date.length == 1) {
      return '0' + date;
    } else {
      return date;
    }
  }
}

@Pipe({
  name: 'formatTimeGTM'
})
export class FormatTimeGTMPipe implements PipeTransform {
  transform(value: string, pattern: string, timezone: string): any {
    if (!value || !pattern || !timezone) {
      return;
    }
    const currentDate = new Date('01/01/2016 ' + value + ':00');
    const patternTime = pattern === '24h' ? 'HH:mm' : 'h:mm a';
    const rezoned = DateTime.local().setZone(timezone).toFormat('ZZ');
    const time = DateTime.fromJSDate(currentDate).toFormat(patternTime);
    return time.toLowerCase() + ' (GMT' + rezoned + ')';
  }
}

@Pipe({
  name: 'dayName'
})
export class DayNamePipe implements PipeTransform {
  transform(weekday: number, lang: string): any {
    if (!weekday || !lang) {
      return;
    }
    const currentDate = new Date();
    const daysDistance = (weekday + 7 - currentDate.getDate()) % 7;
    currentDate.setDate(currentDate.getDate() + daysDistance);
    const dayName = currentDate.toLocaleString(lang.replace('_', '-'), { weekday: 'long' });
    return dayName;
  }
}

@Pipe({
  name: 'formatDateTimeGTM'
})
export class FormatDateTimeGTMPipe implements PipeTransform {
  transform(value: number, patternDate: string, patternTime: string, onlyDate?: boolean): string | null {
    if (!value || !patternDate || !patternTime) {
      return null;
    }
    const patternT = patternTime ? (patternDate === '24h' ? 'HH:mm' : 'h:mm a') : 'HH:mm';
    const dateFormated = DateTime.fromMillis(value).toFormat(patternDate) + ' ' +
      (onlyDate ? '' : DateTime.fromMillis(value).toFormat(patternT) + ' GMT' + DateTime.fromMillis(value).toFormat('Z'));
    return dateFormated;
  }
}
