import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'negativeNumber' })
export class NegativeNumberPipe implements PipeTransform {
  transform(val: number): number {
    return -Math.abs(val);
  }
}
