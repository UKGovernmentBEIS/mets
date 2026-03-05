import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'applicationType',
})
export class ApplicationTypePipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'NEW_PERMIT':
        return 'New Permit';
      case 'TRANSFER':
        return 'Transfer';
      default:
        return null;
    }
  }
}
