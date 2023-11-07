import BigNumber from 'bignumber.js';

import { isPfcWizardComplete } from '../../../approaches/aer/monitoring-approaches.functions';

export function getPfcTotalsPerSourceStreamEmission(sourceStreamEmission) {
  const totalProvidedReportableEmissions = sourceStreamEmission?.providedEmissions?.totalProvidedReportableEmissions;

  const totalReportableEmissions = sourceStreamEmission?.reportableEmissions
    ? sourceStreamEmission.reportableEmissions
    : 0;

  const reportableEmissions = totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0;

  return reportableEmissions;
}

export function getPfcSummariesTotals(sourceStreamEmissions) {
  let totalReportableEmissions = new BigNumber(0);

  sourceStreamEmissions.forEach((sourceStreamEmission) => {
    const reportableEmissions = isPfcWizardComplete(sourceStreamEmission)
      ? getPfcTotalsPerSourceStreamEmission(sourceStreamEmission)
      : 0;

    const bigReportableEmissions = new BigNumber(reportableEmissions);

    totalReportableEmissions = bigReportableEmissions.plus(totalReportableEmissions);
  });

  return totalReportableEmissions.toString();
}

export function getPfcSummaries(sourceStreamEmissions) {
  const totalReportableEmissions = getPfcSummariesTotals(sourceStreamEmissions);

  return [
    {
      approaches: 'Calculation of PFC',
      totalReportableEmissions: totalReportableEmissions,
      totalBiomassEmissions: '0',
      totalNonSustainableBiomassEmissions: '0',
    },
  ];
}
