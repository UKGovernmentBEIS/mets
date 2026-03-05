import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'snakeToKebab',
})
export class SnakeToKebabPipe implements PipeTransform {
  transform(value: string): string {
    if (!value) {
      return '';
    }
    // Make the string lowercase, then replace underscores with hyphens
    return value.toLowerCase().replace(/_/g, '-');
  }
}
