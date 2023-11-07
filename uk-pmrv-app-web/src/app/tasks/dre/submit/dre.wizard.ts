import moment from 'moment';

import {
  CalculationOfCO2ReportingEmissions,
  CalculationOfPFCReportingEmissions,
  Dre,
  FallbackReportingEmissions,
  InherentCO2ReportingEmissions,
  MeasurementOfCO2ReportingEmissions,
  MeasurementOfN2OReportingEmissions,
  ReportableAndBiomassEmission,
  ReportableEmission,
} from 'pmrv-api';

export function isWizardCompleted(dre: Dre) {
  return (
    dre !== undefined &&
    isDeterminationReasonStepCompleted(dre) &&
    isOfficialNoticeReasonStepCompleted(dre) &&
    isMonitoringApproachesStepCompleted(dre) &&
    isReportableEmissionsStepCompleted(dre) &&
    isInformationSourcesStepCompleted(dre) &&
    isFeeStepCompleted(dre)
  );
}

export function isDeterminationReasonStepCompleted(dre: Dre) {
  return (
    dre !== undefined &&
    dre.determinationReason !== undefined &&
    !!dre.determinationReason.type &&
    (dre.determinationReason.type !== 'OTHER' || !!dre.determinationReason.typeOtherSummary)
  );
}

export function isOfficialNoticeReasonStepCompleted(dre: Dre) {
  return dre !== undefined && !!dre.officialNoticeReason;
}

export function isMonitoringApproachesStepCompleted(dre: Dre) {
  return (
    dre !== undefined &&
    dre.monitoringApproachReportingEmissions !== undefined &&
    Object.keys(dre.monitoringApproachReportingEmissions).length > 0 &&
    (!dre.monitoringApproachReportingEmissions.CALCULATION_CO2 ||
      (dre.monitoringApproachReportingEmissions.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions)
        .calculateTransferredCO2 !== undefined) &&
    (!dre.monitoringApproachReportingEmissions.MEASUREMENT_CO2 ||
      (dre.monitoringApproachReportingEmissions.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions)
        .measureTransferredCO2 !== undefined) &&
    (!dre.monitoringApproachReportingEmissions.MEASUREMENT_N2O ||
      (dre.monitoringApproachReportingEmissions.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions)
        .measureTransferredN2O !== undefined)
  );
}

export function isReportableEmissionsStepCompleted(dre: Dre) {
  return (
    dre !== undefined &&
    dre.monitoringApproachReportingEmissions !== undefined &&
    Object.keys(dre.monitoringApproachReportingEmissions)?.length > 0 &&
    isCalculationOfCO2ReportingEmissionsCompleted(dre) &&
    isMeasurementOfCO2ReportingEmissionsCompleted(dre) &&
    isMeasurementOfN2OReportingEmissionsCompleted(dre) &&
    isCalculationOfPFCReportingEmissionsCompleted(dre) &&
    isInherentCO2ReportingEmissionsCompleted(dre) &&
    isFallbackReportingEmissionsCompleted(dre)
  );
}

export function isInformationSourcesStepCompleted(dre: Dre) {
  return dre !== undefined && dre.informationSources?.length > 0;
}

export function isChargeOperatorStepCompleted(dre: Dre) {
  return dre !== undefined && dre.fee !== undefined && dre.fee.chargeOperator !== undefined;
}

export function isFeeStepCompleted(dre: Dre) {
  const todayMin = moment(new Date()).set({ hour: 0, minute: 0, second: 0, millisecond: 0 });

  return (
    dre !== undefined &&
    dre.fee !== undefined &&
    (dre.fee.chargeOperator === false ||
      (dre.fee.chargeOperator === true &&
        !!dre.fee?.feeDetails?.dueDate &&
        moment(dre.fee?.feeDetails?.dueDate).isSameOrAfter(todayMin) &&
        !!dre.fee?.feeDetails?.hourlyRate &&
        !!dre.fee?.feeDetails?.totalBillableHours))
  );
}

export function isCalculationOfCO2ReportingEmissionsCompleted(dre: Dre) {
  const calculationOfCO2ReportingEmissions = dre.monitoringApproachReportingEmissions
    ?.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions;

  return (
    !calculationOfCO2ReportingEmissions ||
    (isReportableAndBiomassEmissionCompleted(calculationOfCO2ReportingEmissions?.combustionEmissions) &&
      isReportableAndBiomassEmissionCompleted(calculationOfCO2ReportingEmissions?.massBalanceEmissions) &&
      isReportableAndBiomassEmissionCompleted(calculationOfCO2ReportingEmissions?.processEmissions) &&
      (calculationOfCO2ReportingEmissions.calculateTransferredCO2 === false ||
        isReportableAndBiomassEmissionCompleted(calculationOfCO2ReportingEmissions?.transferredCO2Emissions)))
  );
}

export function isMeasurementOfCO2ReportingEmissionsCompleted(dre: Dre) {
  const measurementOfCO2ReportingEmissions = dre.monitoringApproachReportingEmissions
    ?.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions;

  return (
    !measurementOfCO2ReportingEmissions ||
    (isReportableAndBiomassEmissionCompleted(measurementOfCO2ReportingEmissions?.emissions) &&
      (measurementOfCO2ReportingEmissions.measureTransferredCO2 === false ||
        isReportableAndBiomassEmissionCompleted(measurementOfCO2ReportingEmissions?.transferredCO2Emissions)))
  );
}

export function isMeasurementOfN2OReportingEmissionsCompleted(dre: Dre) {
  const measurementOfN2OReportingEmissions = dre.monitoringApproachReportingEmissions
    ?.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions;

  return (
    !measurementOfN2OReportingEmissions ||
    (isReportableAndBiomassEmissionCompleted(measurementOfN2OReportingEmissions?.emissions) &&
      (measurementOfN2OReportingEmissions.measureTransferredN2O === false ||
        isReportableAndBiomassEmissionCompleted(measurementOfN2OReportingEmissions?.transferredN2OEmissions)))
  );
}

export function isCalculationOfPFCReportingEmissionsCompleted(dre: Dre) {
  const calculationOfPFCReportingEmissions = dre.monitoringApproachReportingEmissions
    ?.CALCULATION_PFC as CalculationOfPFCReportingEmissions;

  return (
    !calculationOfPFCReportingEmissions ||
    isReportableEmissionCompleted(calculationOfPFCReportingEmissions?.totalEmissions)
  );
}

export function isInherentCO2ReportingEmissionsCompleted(dre: Dre) {
  const inherentCO2ReportingEmissions = dre.monitoringApproachReportingEmissions
    ?.INHERENT_CO2 as InherentCO2ReportingEmissions;

  return !inherentCO2ReportingEmissions || isReportableEmissionCompleted(inherentCO2ReportingEmissions?.totalEmissions);
}

export function isFallbackReportingEmissionsCompleted(dre: Dre) {
  const fallbackReportingEmissions = dre.monitoringApproachReportingEmissions?.FALLBACK as FallbackReportingEmissions;

  return !fallbackReportingEmissions || isReportableAndBiomassEmissionCompleted(fallbackReportingEmissions?.emissions);
}

function isReportableAndBiomassEmissionCompleted(emission: ReportableAndBiomassEmission) {
  return !!emission && emission.reportableEmissions !== undefined && emission.sustainableBiomass !== undefined;
}

function isReportableEmissionCompleted(emission: ReportableEmission) {
  return !!emission && emission.reportableEmissions !== undefined;
}
