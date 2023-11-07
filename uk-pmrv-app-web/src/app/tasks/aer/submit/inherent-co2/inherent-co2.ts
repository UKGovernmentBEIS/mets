import {
  AerApplicationSubmitRequestTaskPayload,
  AerInherentReceivingTransferringInstallation,
  AerMonitoringApproachEmissions,
  InherentCO2Emissions,
  InherentReceivingTransferringInstallation,
} from 'pmrv-api';

export function buildTaskData(
  payload: AerApplicationSubmitRequestTaskPayload,
  newInstallation: InherentReceivingTransferringInstallation,
  index: number,
): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const installations = (monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions)
    ?.inherentReceivingTransferringInstallations;

  const aerInherentInstallations: AerInherentReceivingTransferringInstallation[] =
    installations && installations[index]
      ? installations.map((installation, idx) =>
          index === idx
            ? {
                ...installation,
                inherentReceivingTransferringInstallation: newInstallation,
              }
            : installation,
        )
      : [
          ...(installations ?? []),
          {
            inherentReceivingTransferringInstallation: newInstallation,
          } as AerInherentReceivingTransferringInstallation,
        ];

  return buildAerMonitoringEmissions(monitoringApproachEmissions, aerInherentInstallations);
}

export function removeAndBuildTaskData(
  payload: AerApplicationSubmitRequestTaskPayload,
  index: number,
): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const aerInherentInstallations = (monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions)
    ?.inherentReceivingTransferringInstallations;
  const remainingInstallations = aerInherentInstallations.filter((_, i) => i !== index);

  return buildAerMonitoringEmissions(monitoringApproachEmissions, remainingInstallations);
}

function buildAerMonitoringEmissions(
  monitoringApproachEmissions: { [p: string]: AerMonitoringApproachEmissions },
  aerInherentInstallations: AerInherentReceivingTransferringInstallation[],
): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
  return {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      INHERENT_CO2: {
        type: 'INHERENT_CO2',
        inherentReceivingTransferringInstallations: aerInherentInstallations,
      } as InherentCO2Emissions,
    },
  };
}
