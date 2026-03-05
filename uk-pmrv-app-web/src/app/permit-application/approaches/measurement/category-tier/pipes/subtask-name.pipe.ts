import { Pipe, PipeTransform } from '@angular/core';

import { StatusKey } from '../../../../shared/types/permit-task.type';

@Pipe({
  name: 'subtaskName',
})
export class SubtaskNamePipe implements PipeTransform {
  transform(value: StatusKey): string {
    switch (value) {
      case 'MEASUREMENT_CO2_Measured_Emissions':
        return 'Measured emissions';
      case 'MEASUREMENT_CO2_Applied_Standard':
        return 'Applied standard';
      case 'MEASUREMENT_CO2_Biomass_Fraction':
        return 'Biomass fraction';
      default:
        return null;
    }
  }
}
