import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerEmissionsMonitoringApproach, EmpEmissionsMonitoringApproach } from 'pmrv-api';

const EMP_MONITORING_APPROACH_SELECTION = {
  EUROCONTROL_SUPPORT_FACILITY: 'Use unmodified Eurocontrol Support Facility data',
  EUROCONTROL_SMALL_EMITTERS: 'Use your own flight data with the Eurocontrol Small Emitters Tool',
  FUEL_USE_MONITORING: 'Use fuel use monitoring',
};

const AER_MONITORING_APPROACH_SELECTION = {
  EUROCONTROL_SUPPORT_FACILITY: 'Unmodified Eurocontrol Support Facility data',
  EUROCONTROL_SMALL_EMITTERS: 'Your own flight data with the Eurocontrol Small Emitters Tool',
  FUEL_USE_MONITORING: 'Fuel use monitoring',
};

type Section = 'emp' | 'aer';

@Pipe({
  name: 'monitoringApproachType',
  pure: true,
  standalone: true,
})
export class MonitoringApproachTypePipe implements PipeTransform {
  transform(
    value:
      | EmpEmissionsMonitoringApproach['monitoringApproachType']
      | AviationAerEmissionsMonitoringApproach['monitoringApproachType'],
    section: Section = 'emp',
  ): string | null {
    if (value == null) {
      return null;
    }

    if (section === 'aer') {
      return AER_MONITORING_APPROACH_SELECTION[value] ?? null;
    } else {
      return EMP_MONITORING_APPROACH_SELECTION[value] ?? null;
    }
  }
}
