import { AviationAerCorsia3YearPeriodOffsetting } from 'pmrv-api';

import { AviationAerCorsia3YearPeriodOffsettingTableData } from '../types';

export const getTableData = (
  offsettingRequirements: AviationAerCorsia3YearPeriodOffsetting,
): AviationAerCorsia3YearPeriodOffsettingTableData[] => {
  const yearlyOffsettingData = offsettingRequirements.yearlyOffsettingData;

  const years = offsettingRequirements.schemeYears || [];
  const data = years.map((year) => {
    const yearOffsettingData = yearlyOffsettingData[year];

    return {
      schemeYear: year,
      calculatedAnnualOffsetting: yearOffsettingData?.calculatedAnnualOffsetting,
      cefEmissionsReductions: yearOffsettingData?.cefEmissionsReductions,
    } as unknown as AviationAerCorsia3YearPeriodOffsettingTableData;
  });

  const totals: AviationAerCorsia3YearPeriodOffsettingTableData[] = [
    {
      schemeYear: 'Total (tCO2)',
      calculatedAnnualOffsetting: offsettingRequirements.totalYearlyOffsettingData.calculatedAnnualOffsetting,
      cefEmissionsReductions: offsettingRequirements.totalYearlyOffsettingData.cefEmissionsReductions,
    },
    {
      schemeYear: 'Period offsetting requirements (tCO2)',
      calculatedAnnualOffsetting: null,
      cefEmissionsReductions: offsettingRequirements.periodOffsettingRequirements,
    },
  ];

  return [...data, ...totals];
};
