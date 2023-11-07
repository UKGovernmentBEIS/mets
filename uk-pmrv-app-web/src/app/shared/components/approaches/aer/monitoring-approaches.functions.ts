import {
  CalculationSourceStreamEmission,
  MeasurementCO2EmissionPointEmission,
  MeasurementN2OEmissionPointEmission,
  PfcSourceStreamEmission,
} from 'pmrv-api';

function areTransferredWizardStepsFilled(
  emission: CalculationSourceStreamEmission | MeasurementCO2EmissionPointEmission | MeasurementN2OEmissionPointEmission,
  hasTransfer: boolean,
): boolean {
  return (
    !hasTransfer ||
    emission?.transfer?.entryAccountingForTransfer === false ||
    !!emission?.transfer?.installationDetails ||
    !!emission?.transfer?.installationEmitter
  );
}

export function isCalculationWizardComplete(
  sourceStreamEmission: CalculationSourceStreamEmission,
  hasTransfer: boolean,
): boolean {
  const calculationCorrect = (sourceStreamEmission?.parameterCalculationMethod as any)?.emissionCalculationParamValues
    ?.calculationCorrect;

  return (
    calculationCorrect !== undefined &&
    calculationCorrect !== null &&
    areTransferredWizardStepsFilled(sourceStreamEmission, hasTransfer)
  );
}

export function isMeasurementWizardComplete(
  emissionPointEmission: MeasurementCO2EmissionPointEmission | MeasurementN2OEmissionPointEmission,
  hasTransfer: boolean,
): boolean {
  const calculationCorrect = emissionPointEmission?.calculationCorrect;

  return (
    calculationCorrect !== undefined &&
    calculationCorrect !== null &&
    areTransferredWizardStepsFilled(emissionPointEmission, hasTransfer)
  );
}

export function isPfcWizardComplete(sourceStreamEmission: PfcSourceStreamEmission): boolean {
  const calculationCorrect = sourceStreamEmission?.calculationCorrect;

  return calculationCorrect !== undefined && calculationCorrect !== null;
}
