import { AviationAerDataGap } from 'pmrv-api';

export function calculateAffectedFlightsPercentage(
  totalAggregatedFlights: number,
  dataGaps: AviationAerDataGap[],
): string {
  let affectedFlightsPercentage = 0;
  const sumOfFlightsAffected = calculateAffectedFlightsDataGaps(dataGaps);

  if (totalAggregatedFlights !== 0) {
    affectedFlightsPercentage = (sumOfFlightsAffected / totalAggregatedFlights) * 100;
  }

  return affectedFlightsPercentage.toFixed(1);
}

export function calculateAffectedFlightsDataGaps(dataGaps: AviationAerDataGap[]) {
  return dataGaps.reduce((sum, current) => sum + current.flightsAffected, 0);
}
