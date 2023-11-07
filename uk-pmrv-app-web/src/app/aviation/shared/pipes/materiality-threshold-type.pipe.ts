import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerCorsiaVerifiersConclusions } from 'pmrv-api';

@Pipe({
  name: 'materialityThresholdType',
  pure: true,
  standalone: true,
})
export class MaterialityThresholdTypePipe implements PipeTransform {
  transform(value: AviationAerCorsiaVerifiersConclusions['materialityThresholdType']): string | null {
    switch (value) {
      case 'THRESHOLD_2_PER_CENT':
        return '2%';
      case 'THRESHOLD_5_PER_CENT':
        return '5%';
      default:
        return null;
    }
  }
}
