import { InherentReceivingTransferringInstallation } from 'pmrv-api';

export function isWizardComplete(installation: InherentReceivingTransferringInstallation): boolean {
  const isDirectionStepCompleted = installation?.inherentCO2Direction !== undefined;
  const isDetailsStepCompleted =
    (installation?.inherentReceivingTransferringInstallationDetailsType as any)?.installationDetails !== undefined ||
    (installation?.inherentReceivingTransferringInstallationDetailsType as any)?.installationEmitter !== undefined;

  const isInstrumentsStepCompleted = installation?.measurementInstrumentOwnerTypes?.length > 0;
  const isEmissionsStepCompleted = installation?.totalEmissions !== undefined;

  return (
    installation &&
    isDirectionStepCompleted &&
    isDetailsStepCompleted &&
    isInstrumentsStepCompleted &&
    isEmissionsStepCompleted
  );
}
