import { FuelMonitoringApproach, SmallEmittersMonitoringApproach, SupportFacilityMonitoringApproach } from 'pmrv-api';

export type SimplifiedMonitoringApproach = SmallEmittersMonitoringApproach | SupportFacilityMonitoringApproach;
export type EmissionsMonitoringApproach = FuelMonitoringApproach & SimplifiedMonitoringApproach;
