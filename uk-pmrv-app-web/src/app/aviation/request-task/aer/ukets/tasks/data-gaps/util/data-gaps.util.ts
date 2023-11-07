import { AviationAerDataGap, AviationAerUkEtsAggregatedEmissionDataDetails } from 'pmrv-api';

export function calculateAffectedFlightsPercentage(
  aggregatedEmissionsDataDetails: AviationAerUkEtsAggregatedEmissionDataDetails[],
  dataGaps: AviationAerDataGap[],
): string {
  let affectedFlightsPercentage = 0;
  const sumOfFlightsAffected = dataGaps.reduce((sum, current) => sum + current.flightsAffected, 0);

  const totalAggregatedFlights = aggregatedEmissionsDataDetails.reduce(
    (sum, current) => sum + current.flightsNumber,
    0,
  );

  if (totalAggregatedFlights !== 0) {
    affectedFlightsPercentage = (sumOfFlightsAffected / totalAggregatedFlights) * 100;
  }

  return affectedFlightsPercentage.toFixed(1);
}
