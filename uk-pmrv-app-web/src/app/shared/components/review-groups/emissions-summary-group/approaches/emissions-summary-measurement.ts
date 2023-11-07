import BigNumber from 'bignumber.js';

import { isMeasurementWizardComplete } from '../../../approaches/aer/monitoring-approaches.functions';
import { getNonSustainableBiomassEmissions } from '../emissions-summary-group';

export function calculateSummariesPerEmissionPointEmission(emissionPointEmission, measurementHasTransfer) {
  const totalProvidedReportableEmissions = emissionPointEmission?.providedEmissions?.totalProvidedReportableEmissions;

  const totalProvidedSustainableBiomassEmissions =
    emissionPointEmission?.providedEmissions?.totalProvidedSustainableBiomassEmissions;

  const totalReportableEmissions = emissionPointEmission?.reportableEmissions
    ? emissionPointEmission.reportableEmissions
    : 0;

  const totalSustainableBiomassEmissions = emissionPointEmission?.sustainableBiomassEmissions
    ? emissionPointEmission?.sustainableBiomassEmissions
    : 0;

  let reportableEmissions = totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0;
  let sustainableBiomassEmissions = totalProvidedSustainableBiomassEmissions ?? totalSustainableBiomassEmissions ?? 0;
  let nonSustainableBiomassEmissions: BigNumber | string = emissionPointEmission?.biomassPercentages?.contains
    ? getNonSustainableBiomassEmissions(
        reportableEmissions,
        sustainableBiomassEmissions,
        emissionPointEmission?.biomassPercentages?.nonSustainableBiomassPercentage,
      )
    : new BigNumber(0);

  if (measurementHasTransfer && !!emissionPointEmission?.transfer?.entryAccountingForTransfer) {
    if (emissionPointEmission?.transfer?.transferDirection === 'RECEIVED_FROM_ANOTHER_INSTALLATION') {
      reportableEmissions = Math.abs(reportableEmissions).toString();
      sustainableBiomassEmissions = Math.abs(sustainableBiomassEmissions).toString();
      nonSustainableBiomassEmissions = nonSustainableBiomassEmissions.abs().toString();
    } else {
      reportableEmissions = -Math.abs(reportableEmissions);
      sustainableBiomassEmissions = -Math.abs(sustainableBiomassEmissions);
      nonSustainableBiomassEmissions = (-nonSustainableBiomassEmissions.abs()).toString();
    }
  }

  return [reportableEmissions, sustainableBiomassEmissions, nonSustainableBiomassEmissions];
}

export function calculateEmissionPointEmissionsSummaries(emissionPointEmissions, measurementHasTransfer) {
  let totalReportableEmissions = new BigNumber(0);
  let totalBiomassEmissions = new BigNumber(0);
  let totalNonSustainableBiomassEmissions = new BigNumber(0);

  emissionPointEmissions.forEach((emissionPointEmission) => {
    const [reportableEmissions, biomassEmissions, nonSustainableBiomassEmissions] = isMeasurementWizardComplete(
      emissionPointEmission,
      measurementHasTransfer,
    )
      ? calculateSummariesPerEmissionPointEmission(emissionPointEmission, measurementHasTransfer)
      : [0, 0, 0];

    const bigReportableEmissions = new BigNumber(reportableEmissions);
    const bigBiomassEmissions = new BigNumber(biomassEmissions);
    const bigNonSustainableBiomassEmissions = new BigNumber(nonSustainableBiomassEmissions);

    totalReportableEmissions = bigReportableEmissions.plus(totalReportableEmissions);
    totalBiomassEmissions = bigBiomassEmissions.plus(totalBiomassEmissions);
    totalNonSustainableBiomassEmissions = bigNonSustainableBiomassEmissions.plus(totalNonSustainableBiomassEmissions);
  });

  return [
    totalReportableEmissions.toString(),
    totalBiomassEmissions.toString(),
    totalNonSustainableBiomassEmissions.toFixed(5).replace(/0+$/, '').replace(/\.$/, ''),
  ];
}

export function getMeasurementSummaries(emissionPointEmissions, measurementHasTransfer, measurementType) {
  const countableEmissionPointEmissions = !measurementHasTransfer
    ? emissionPointEmissions
    : emissionPointEmissions.filter((emissionPointEmission) => {
        return !emissionPointEmission?.transfer?.entryAccountingForTransfer;
      });

  const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
    calculateEmissionPointEmissionsSummaries(countableEmissionPointEmissions, measurementHasTransfer);

  return [
    {
      approaches: measurementType === 'MEASUREMENT_CO2' ? 'Measurement of CO2' : 'Measurement of N2O',
      totalReportableEmissions: totalReportableEmissions,
      totalBiomassEmissions: totalBiomassEmissions,
      totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
    },
  ];
}

export function getMeasurementTransferredSummaries(emissionPointEmissions, measurementHasTransfer, measurementType) {
  const countableEmissionPointEmissions = emissionPointEmissions.filter((emissionPointEmission) => {
    return emissionPointEmission?.transfer?.entryAccountingForTransfer;
  });

  const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
    calculateEmissionPointEmissionsSummaries(countableEmissionPointEmissions, measurementHasTransfer);

  return [
    {
      approaches:
        measurementType === 'MEASUREMENT_CO2' ? 'Measurement of transferred CO2' : 'Measurement of transferred N2O',
      totalReportableEmissions: totalReportableEmissions,
      totalBiomassEmissions: totalBiomassEmissions,
      totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
    },
  ];
}
