import { TemplateEmissionsSummary } from '@shared/components/review-groups/emissions-summary-group/interfaces/template-emissions-summary.interface';
import BigNumber from 'bignumber.js';

import { isCalculationWizardComplete } from '../../../approaches/aer/monitoring-approaches.functions';
import {
  combustionCalculationSourceStreamTypes,
  getNonSustainableBiomassEmissions,
  massBalanceCalculationSourceStreamTypes,
  processCalculationSourceStreamTypes,
} from '../emissions-summary-group';

export function calculateSummariesPerSourceStreamEmission(sourceStreamEmission, calculationsHasTransfer) {
  const emissionCalculationParamValues =
    sourceStreamEmission?.parameterCalculationMethod?.emissionCalculationParamValues;

  const totalProvidedReportableEmissions =
    emissionCalculationParamValues?.providedEmissions?.totalProvidedReportableEmissions;

  const totalProvidedSustainableBiomassEmissions =
    emissionCalculationParamValues?.providedEmissions?.totalProvidedSustainableBiomassEmissions;

  const totalReportableEmissions = emissionCalculationParamValues?.totalReportableEmissions
    ? emissionCalculationParamValues.totalReportableEmissions
    : 0;

  const totalSustainableBiomassEmissions = emissionCalculationParamValues?.totalSustainableBiomassEmissions
    ? emissionCalculationParamValues?.totalSustainableBiomassEmissions
    : 0;

  let reportableEmissions = totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0;
  let sustainableBiomassEmissions = totalProvidedSustainableBiomassEmissions ?? totalSustainableBiomassEmissions ?? 0;
  let nonSustainableBiomassEmissions: BigNumber | string = sourceStreamEmission?.biomassPercentages?.contains
    ? getNonSustainableBiomassEmissions(
        reportableEmissions,
        sustainableBiomassEmissions,
        sourceStreamEmission?.biomassPercentages?.nonSustainableBiomassPercentage,
      )
    : new BigNumber(0);

  if (calculationsHasTransfer && !!sourceStreamEmission?.transfer?.entryAccountingForTransfer) {
    if (sourceStreamEmission?.transfer?.transferDirection === 'RECEIVED_FROM_ANOTHER_INSTALLATION') {
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

export function calculateSourceStreamEmissionsSummaries(sourceStreamEmissions, calculationsHasTransfer) {
  let totalReportableEmissions = new BigNumber(0);
  let totalBiomassEmissions = new BigNumber(0);
  let totalNonSustainableBiomassEmissions = new BigNumber(0);

  sourceStreamEmissions.forEach((sourceStreamEmission) => {
    const [reportableEmissions, biomassEmissions, nonSustainableBiomassEmissions] = isCalculationWizardComplete(
      sourceStreamEmission,
      calculationsHasTransfer,
    )
      ? calculateSummariesPerSourceStreamEmission(sourceStreamEmission, calculationsHasTransfer)
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
    totalNonSustainableBiomassEmissions.toString(),
  ];
}

export function getCalculationSummaries(
  sourceStreams,
  sourceStreamEmissions,
  calculationsHasTransfer,
): TemplateEmissionsSummary[] {
  const getSourceStreamEmissionsByType = (sourceStreams, sourceStreamTypes, sourceStreamEmissions) => {
    const filteredSourceStreams = sourceStreams
      .filter((sourceStream) => sourceStreamTypes.includes(sourceStream.type))
      ?.map((sourceStream) => sourceStream.id);

    return !sourceStreamEmissions
      ? []
      : sourceStreamEmissions.filter((sourceStreamEmission) => {
          return filteredSourceStreams.includes(sourceStreamEmission.sourceStream);
        });
  };

  const getCombustionSummary = (sourceStreamEmissions) => {
    const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
      calculateSourceStreamEmissionsSummaries(sourceStreamEmissions, calculationsHasTransfer);

    return [
      {
        approaches: 'Calculation of combustion',
        totalReportableEmissions: totalReportableEmissions,
        totalBiomassEmissions: totalBiomassEmissions,
        totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
      },
    ];
  };

  const getProcessSummary = (sourceStreamEmissions) => {
    const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
      calculateSourceStreamEmissionsSummaries(sourceStreamEmissions, calculationsHasTransfer);

    return [
      {
        approaches: 'Calculation of process',
        totalReportableEmissions: totalReportableEmissions,
        totalBiomassEmissions: totalBiomassEmissions,
        totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
      },
    ];
  };

  const getMassBalanceSummary = (sourceStreamEmissions) => {
    const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
      calculateSourceStreamEmissionsSummaries(sourceStreamEmissions, calculationsHasTransfer);

    return [
      {
        approaches: 'Calculation of mass balance',
        totalReportableEmissions: totalReportableEmissions,
        totalBiomassEmissions: totalBiomassEmissions,
        totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
      },
    ];
  };

  const countableSourceStreamEmissions = !calculationsHasTransfer
    ? sourceStreamEmissions
    : sourceStreamEmissions.filter((sourceStreamEmission) => {
        return !sourceStreamEmission?.transfer?.entryAccountingForTransfer;
      });

  const combustionSourceStreamEmissions = getSourceStreamEmissionsByType(
    sourceStreams,
    combustionCalculationSourceStreamTypes,
    countableSourceStreamEmissions,
  );

  const processSourceStreamEmissions = getSourceStreamEmissionsByType(
    sourceStreams,
    processCalculationSourceStreamTypes,
    countableSourceStreamEmissions,
  );

  const massBalanceSourceStreamEmissions = getSourceStreamEmissionsByType(
    sourceStreams,
    massBalanceCalculationSourceStreamTypes,
    countableSourceStreamEmissions,
  );

  return [
    ...(combustionSourceStreamEmissions.length ? getCombustionSummary(combustionSourceStreamEmissions) : []),
    ...(processSourceStreamEmissions.length ? getProcessSummary(processSourceStreamEmissions) : []),
    ...(massBalanceSourceStreamEmissions.length ? getMassBalanceSummary(massBalanceSourceStreamEmissions) : []),
  ];
}

export function getCalculationTransferredSummaries(sourceStreamEmissions, calculationsHasTransfer) {
  const countableSourceStreamEmissions = sourceStreamEmissions.filter((sourceStreamEmission) => {
    return sourceStreamEmission?.transfer?.entryAccountingForTransfer;
  });

  const [totalReportableEmissions, totalBiomassEmissions, totalNonSustainableBiomassEmissions] =
    calculateSourceStreamEmissionsSummaries(countableSourceStreamEmissions, calculationsHasTransfer);

  return [
    {
      approaches: 'Calculation of transferred CO2',
      totalReportableEmissions: totalReportableEmissions,
      totalBiomassEmissions: totalBiomassEmissions,
      totalNonSustainableBiomassEmissions: totalNonSustainableBiomassEmissions,
    },
  ];
}
