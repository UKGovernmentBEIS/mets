import { Pipe, PipeTransform } from '@angular/core';

import { AirImprovement, PermitMonitoringApproachSection } from 'pmrv-api';

@Pipe({ name: 'monitoringApproachDescription' })
export class MonitoringApproachDescriptionPipe implements PipeTransform {
  transform(value: PermitMonitoringApproachSection['type'] | AirImprovement['type']): string {
    switch (value) {
      case 'CALCULATION_CO2':
        return 'Calculation of CO2';
      case 'MEASUREMENT_CO2':
        return 'Measurement of CO2';
      case 'FALLBACK':
        return 'Fallback approach';
      case 'MEASUREMENT_N2O':
        return 'Measurement of nitrous oxide (N2O)';
      case 'CALCULATION_PFC':
        return 'Calculation of perfluorocarbons (PFC)';
      case 'INHERENT_CO2':
        return 'Inherent CO2 emissions';
      case 'TRANSFERRED_CO2_N2O':
        return 'Procedures for transferred CO2 or N2O';
    }
  }
}
