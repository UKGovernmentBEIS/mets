import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'negativeNumber',
  standalone: false,
})
export class NegativeNumberPipe implements PipeTransform {
  transform(val: number): number {
    return -Math.abs(val);
  }
}
