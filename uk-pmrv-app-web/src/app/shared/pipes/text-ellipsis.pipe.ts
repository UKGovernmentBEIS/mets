import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'textEllipsis',
})
export class TextEllipsisPipe implements PipeTransform {
  transform(value: string | null, numOfChars: number = 100): unknown {
    if (numOfChars && value?.length > numOfChars) {
      return value.substring(0, numOfChars).concat('...');
    }
    return value;
  }
}
