import { Pipe, PipeTransform } from '@angular/core';

import { CalculationActivityDataCalculationMethod } from 'pmrv-api';

@Pipe({ name: 'activityCalculationMethodType' })
export class ActivityCalculationMethodTypePipe implements PipeTransform {
  transform(value: CalculationActivityDataCalculationMethod['type']): string {
    switch (value) {
      case 'CONTINUOUS_METERING':
        return 'Continuous metering';
      case 'AGGREGATION_OF_METERING_QUANTITIES':
        return 'Aggregation of metering quantities';
      default:
        return '';
    }
  }
}
