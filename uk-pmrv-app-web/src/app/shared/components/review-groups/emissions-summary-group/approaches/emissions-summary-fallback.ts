import { TemplateEmissionsSummary } from '@shared/components/review-groups/emissions-summary-group/interfaces/template-emissions-summary.interface';
import BigNumber from 'bignumber.js';

import { FallbackEmissions } from 'pmrv-api';

/**
 * Fallback emissions
 * Reportable = "Total fossil emissions" + "Total non-sustainable biomass emissions" (Precalculated within the model)
 * Sustainable emissions =  "Total sustainable biomass emissions"
 *
 * @param fallbackEmissions
 * @return TemplateEmissionsSummary[]
 */
export function getFallbackSummaries(fallbackEmissions: FallbackEmissions): TemplateEmissionsSummary[] {
  const [totalReportableEmissions, totalBiomassEmissions] = getFallbackSummariesTotals(fallbackEmissions);

  return [
    {
      approaches: 'Fallback',
      totalReportableEmissions: totalReportableEmissions.toString(),
      totalBiomassEmissions: totalBiomassEmissions.toString(),
      totalNonSustainableBiomassEmissions: fallbackEmissions?.biomass?.totalNonSustainableBiomassEmissions,
    },
  ];
}

/**
 * Calculates the total of Fallback emissions,
 * ReportableEmissions = Precalculated within the model
 * BiomassEmissions = Available when 'biomass.contains' == true
 *
 * Returns Array with 2 values for totalReportableEmissions, totalBiomassEmissions
 *
 * @param fallbackEmissions
 * @return BigNumber[]
 */
export function getFallbackSummariesTotals(fallbackEmissions: FallbackEmissions): BigNumber[] {
  return [
    new BigNumber(fallbackEmissions?.reportableEmissions ?? 0),
    new BigNumber(fallbackEmissions?.biomass?.totalSustainableBiomassEmissions ?? 0),
  ];
}
