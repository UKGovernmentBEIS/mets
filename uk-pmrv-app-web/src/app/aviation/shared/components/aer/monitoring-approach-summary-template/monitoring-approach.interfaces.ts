import {
  AviationAerEmissionsMonitoringApproach,
  AviationAerSmallEmittersMonitoringApproach,
  AviationAerSupportFacilityMonitoringApproach,
} from 'pmrv-api';

export type AerMonitoringApproach =
  | AviationAerSmallEmittersMonitoringApproach
  | AviationAerSupportFacilityMonitoringApproach;

export type EmissionsMonitoringApproach = AviationAerEmissionsMonitoringApproach & AerMonitoringApproach;

export interface EmissionSmallEmittersSupportFacilityFormValues {
  monitoringApproachType: AviationAerEmissionsMonitoringApproach['monitoringApproachType'] | null;
  totalEmissionsType: AviationAerSupportFacilityMonitoringApproach['totalEmissionsType'] | null;
  fullScopeTotalEmissions: string | null;
  aviationActivityTotalEmissions: string | null;
  numOfFlightsJanApr: number | null;
  numOfFlightsMayAug: number | null;
  numOfFlightsSepDec: number | null;
  totalEmissions: string | null;
}
