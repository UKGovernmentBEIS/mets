import { TemplateEmissionsSummary } from '@shared/components/review-groups/emissions-summary-group/interfaces/template-emissions-summary.interface';
import BigNumber from 'bignumber.js';

import { AerInherentReceivingTransferringInstallation } from 'pmrv-api';

/**
 * InherentCO2 emissions will always have
 * NonSustainableBiomassEmissions = 0
 * BiomassEmissions = 0 (Intermediately always 0, but reserved for future usage/compatibility)
 *
 * @param aerInstallations
 * @return AerInherentReceivingTransferringInstallation[]
 */
export function getInherentCO2Summaries(
  aerInstallations: AerInherentReceivingTransferringInstallation[],
): TemplateEmissionsSummary[] {
  const [totalReportableEmissions, totalBiomassEmissions] = getInherentSummariesTotals(aerInstallations);

  return [
    {
      approaches: 'Inherent CO2',
      totalReportableEmissions: totalReportableEmissions.toString(),
      totalBiomassEmissions: totalBiomassEmissions.toString(),
      totalNonSustainableBiomassEmissions: '0',
    },
  ];
}

/**
 * Calculates the total of InherentCO2 emissions, negates the value when Direction !== 'RECEIVED_FROM_ANOTHER_INSTALLATION'
 * BiomassEmissions = 0, but reserved for future usage/compatibility with other SummariesTotals
 *
 * @param aerInstallations
 * @return BigNumber[] - Array with 2 values for totalReportableEmissions, totalBiomassEmissions
 */
export function getInherentSummariesTotals(
  aerInstallations: AerInherentReceivingTransferringInstallation[],
): BigNumber[] {
  const totalReportableEmissions = aerInstallations.reduce((accumulator, installation) => {
    const emissionValue = new BigNumber(
      installation?.inherentReceivingTransferringInstallation?.totalEmissions ?? 0,
    ).abs();
    const currentValue =
      installation?.inherentReceivingTransferringInstallation?.inherentCO2Direction ===
      'RECEIVED_FROM_ANOTHER_INSTALLATION'
        ? emissionValue
        : emissionValue.negated();
    return accumulator.plus(currentValue);
  }, new BigNumber('0'));

  return [totalReportableEmissions, new BigNumber(0)];
}
