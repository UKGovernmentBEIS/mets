import { InherentCO2Emissions } from 'pmrv-api';

export function getInherentInstallations(payload) {
  return (
    payload.aer?.monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions
  )?.inherentReceivingTransferringInstallations.map((item) => item.inherentReceivingTransferringInstallation);
}
