import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'defaultIfEmpty',
  standalone: false,
})
export class DefaultIfEmptyPipe implements PipeTransform {
  transform(value: any, replacement: string = '-'): string {
    return value ? value : replacement;
  }
}
