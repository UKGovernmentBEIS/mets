import {
  EmpEmissionsMonitoringApproachCorsia,
  FuelMonitoringApproach,
  SmallEmittersMonitoringApproach,
  SupportFacilityMonitoringApproach,
} from 'pmrv-api';

export type SimplifiedMonitoringApproach = SmallEmittersMonitoringApproach | SupportFacilityMonitoringApproach;
export type EmissionsMonitoringApproach = FuelMonitoringApproach &
  SimplifiedMonitoringApproach &
  EmpEmissionsMonitoringApproachCorsia;

export type MonitoringApproachType = EmissionsMonitoringApproach['monitoringApproachType'] &
  EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'];
