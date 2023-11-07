import { PermitMonitoringApproachSection } from 'pmrv-api';

export const monitoringApproachTypeOptions: PermitMonitoringApproachSection['type'][] = [
  'CALCULATION_CO2',
  'MEASUREMENT_CO2',
  'FALLBACK',
  'MEASUREMENT_N2O',
  'CALCULATION_PFC',
  'INHERENT_CO2',
  'TRANSFERRED_CO2_N2O',
];
