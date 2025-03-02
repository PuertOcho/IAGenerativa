import { Pipe, PipeTransform } from '@angular/core';
/** * Generated class for the SortByPipe pipe. * * See https://angular.io/api/core/Pipe for more info on Angular Pipes. */
@Pipe({ name: 'sortBy', })

export class SortByPipe implements PipeTransform {
  /**
   * Takes a value and makes it lowercase.
   */
  transform(array: Array<string>, args: string[]): Array<string> {
    if (array && array.length > 0) {
      array.sort((a: any, b: any) => {
        if (a < b) {
          return -1;
        } else if (a > b) {
          return 1;
        } else {
          return 0;
        }
      });
    }
    return args && args.length > 1 && args[1] == 'desc' ? array.reverse() : array;
  }
}
