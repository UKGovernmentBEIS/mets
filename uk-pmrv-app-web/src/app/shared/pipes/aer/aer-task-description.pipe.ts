import { Pipe, PipeTransform } from '@angular/core';

import { AerSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { MonitoringApproachEmissionDescriptionPipe } from '../monitoring-approach-emission-description.pipe';

@Pipe({
  name: 'aerTaskDescription',
})
export class AerTaskDescriptionPipe implements PipeTransform {
  transform(value: AerSaveReviewGroupDecisionRequestTaskActionPayload['group']): string {
    switch (value) {
      case 'INSTALLATION_DETAILS':
        return 'Installation details';

      case 'FUELS_AND_EQUIPMENT':
        return 'Fuels and equipment inventory';

      case 'ADDITIONAL_INFORMATION':
        return 'Additional information';

      case 'EMISSIONS_SUMMARY':
        return 'Emissions summary';

      case 'ACTIVITY_LEVEL_REPORT':
        return 'Activity level report';

      case 'CALCULATION_CO2':
      case 'MEASUREMENT_CO2':
      case 'MEASUREMENT_N2O':
      case 'FALLBACK':
      case 'CALCULATION_PFC':
      case 'INHERENT_CO2': {
        const description = new MonitoringApproachEmissionDescriptionPipe();
        return description.transform(value);
      }

      default:
        break;
    }
  }
}
