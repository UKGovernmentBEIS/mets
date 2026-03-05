import { Pipe, PipeTransform } from '@angular/core';

import { CertMonitoringApproach, EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

const EMP_MONITORING_APPROACH_SELECTION = {
  CERT_MONITORING: 'CORSIA CO2 estimation and reporting tool (CERT)',
  FUEL_USE_MONITORING: 'Fuel use monitoring',
};

const EMP_CERT_MONITORING_APPROACH = {
  GREAT_CIRCLE_DISTANCE: 'Great circle distance',
  BLOCK_TIME: 'Block-time',
};

@Pipe({
  name: 'monitoringApproachCorsiaTypes',
  pure: true,
  standalone: true,
})
export class MonitoringApproachTypeCorsiaPipe implements PipeTransform {
  transform(
    value: EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'] | CertMonitoringApproach['certEmissionsType'],
    isMonitoringApproach = true,
  ): string | null {
    if (value == null) {
      return null;
    }
    if (isMonitoringApproach) {
      return EMP_MONITORING_APPROACH_SELECTION[value] ?? null;
    }

    return EMP_CERT_MONITORING_APPROACH[value] ?? null;
  }
}
