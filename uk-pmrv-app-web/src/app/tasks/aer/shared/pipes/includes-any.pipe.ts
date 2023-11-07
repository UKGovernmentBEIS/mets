import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'includesAny',
})
export class IncludesAnyPipe implements PipeTransform {
  transform(sourceArray: any[], values: any[]): boolean {
    return values.some((value) => sourceArray.includes(value));
  }
}
