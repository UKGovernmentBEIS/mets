import { Pipe, PipeTransform } from '@angular/core';

import { BDR } from 'pmrv-api';

@Pipe({
  name: 'statusApplicationType',
  standalone: true,
})
export class StatusApplicationTypePipe implements PipeTransform {
  transform(value: BDR['statusApplicationType']): string {
    switch (value) {
      case 'NONE':
        return 'No';
      case 'HSE':
        return 'Yes, I am applying for HSE status';
      case 'USE':
        return 'Yes, I am applying for USE status';

      default:
        return '';
    }
  }
}
