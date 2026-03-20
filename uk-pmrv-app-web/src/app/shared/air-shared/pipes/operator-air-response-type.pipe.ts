import { Pipe, PipeTransform } from '@angular/core';

import { OperatorAirImprovementResponse } from 'pmrv-api';

@Pipe({
  name: 'operatorAirResponseType',
  standalone: false,
})
export class OperatorAirResponseTypePipe implements PipeTransform {
  transform(type: OperatorAirImprovementResponse['type']): string {
    switch (type) {
      case 'YES':
        return 'Yes';
      case 'NO':
        return 'No';
      case 'ALREADY_MADE':
        return 'Improvement already made';
      default:
        return '';
    }
  }
}
