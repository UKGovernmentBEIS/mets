import { MonitoringApproachEmissionDescriptionPipe } from '@shared/pipes/monitoring-approach-emission-description.pipe';

import { PermitMonitoringApproachSection } from 'pmrv-api';

export function monitoringApproachMap(approach: string) {
  const approachDescriptionPipe = new MonitoringApproachEmissionDescriptionPipe();
  const approachName = approachDescriptionPipe.transform(approach as PermitMonitoringApproachSection['type']);

  switch (approach) {
    case 'CALCULATION_CO2':
      return { link: 'calculation-emissions', linkText: approachName, status: 'CALCULATION_CO2' };
    case 'MEASUREMENT_CO2':
      return { link: 'measurement-co2', linkText: approachName, status: 'MEASUREMENT_CO2' };
    case 'FALLBACK':
      return { link: 'fallback', linkText: approachName, status: 'FALLBACK' };
    case 'MEASUREMENT_N2O':
      return { link: 'measurement-n2o', linkText: approachName, status: 'MEASUREMENT_N2O' };
    case 'CALCULATION_PFC':
      return { link: 'pfc', linkText: approachName, status: 'CALCULATION_PFC' };
    case 'INHERENT_CO2':
      return { link: 'inherent-co2-emissions', linkText: approachName, status: 'INHERENT_CO2' };
    case 'TRANSFERRED_CO2_N2O':
      return { link: 'transferred-co2-emissions', linkText: approachName, status: 'transferred' };
  }

  return approach;
}
