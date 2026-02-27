import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'submitIfEmpty',
  standalone: false,
})
export class SubmitIfEmptyPipe implements PipeTransform {
  transform(value: any): string {
    return value ? 'Save' : 'Submit';
  }
}
