import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export function isWizardComplete(payload: AerApplicationSubmitRequestTaskPayload): boolean {
  const fallbackEmissions = payload?.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;
  const isEmissionNetworkStepCompleted =
    fallbackEmissions?.sourceStreams?.length && fallbackEmissions?.biomass?.contains !== undefined;
  const isDescriptionStepCompleted = !!fallbackEmissions?.description;
  const isUploadDocumentsStepCompleted = !!fallbackEmissions?.files?.length;

  return (
    isEmissionNetworkStepCompleted &&
    isDescriptionStepCompleted &&
    isTotalEmissionsStepCompleted(fallbackEmissions) &&
    isUploadDocumentsStepCompleted
  );
}

function isTotalEmissionsStepCompleted(fallbackEmissions: FallbackEmissions): boolean {
  return fallbackEmissions?.biomass?.contains
    ? !!fallbackEmissions?.totalFossilEmissions &&
        !!fallbackEmissions?.totalFossilEnergyContent &&
        !!fallbackEmissions?.biomass?.totalSustainableBiomassEmissions &&
        !!fallbackEmissions?.biomass?.totalNonSustainableBiomassEmissions &&
        !!fallbackEmissions?.biomass?.totalEnergyContentFromBiomass
    : !!fallbackEmissions?.totalFossilEmissions && !!fallbackEmissions?.totalFossilEnergyContent;
}
