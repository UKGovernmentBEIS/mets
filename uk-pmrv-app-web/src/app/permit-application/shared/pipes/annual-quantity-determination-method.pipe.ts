import { Pipe, PipeTransform } from '@angular/core';

import { AnnualProductionLevel } from 'pmrv-api';

@Pipe({
  name: 'annualQuantityDeterminationMethod',
})
export class AnnualQuantityDeterminationMethodPipe implements PipeTransform {
  transform(value: AnnualProductionLevel['annualQuantityDeterminationMethod']): string {
    switch (value) {
      case 'CONTINUAL_METERING_PROCESS':
        return '5. (a) based on continual metering at the process where the material is consumed or produced';
      case 'AGGREGATION_METERING_QUANTITIES':
        return '5. (b) based on aggregation of metering of quantities separately delivered or produced taking into account relevant stock changes';
      default:
        return '';
    }
  }
}
