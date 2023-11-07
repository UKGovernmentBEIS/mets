import BigNumber from 'bignumber.js';

import {
  CalculationOfCO2ReportingEmissions,
  CalculationOfPFCReportingEmissions,
  DreMonitoringApproachReportingEmissions,
  FallbackReportingEmissions,
  InherentCO2ReportingEmissions,
  MeasurementOfCO2ReportingEmissions,
  MeasurementOfN2OReportingEmissions,
} from 'pmrv-api';

import { format } from '../../../shared/utils/bignumber.utils';

export type emissionTypeKey = 'reportableEmissions' | 'sustainableBiomass';

export function calculateTotalReportableEmissionsAmount(emissions: {
  [key: string]: DreMonitoringApproachReportingEmissions;
}): string {
  const emissionType: emissionTypeKey = 'reportableEmissions';

  return format(
    calculationOfCO2ReportableEmissionsAmount(
      emissions?.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions,
      emissionType,
    )
      .plus(
        measurementOfCO2ReportableEmissionsAmount(
          emissions?.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions,
          emissionType,
        ),
      )
      .plus(
        measurementOfN2OReportableEmissionsAmount(
          emissions?.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions,
          emissionType,
        ),
      )
      .plus(calculationOfPFCReportableEmissionsAmount(emissions?.CALCULATION_PFC as CalculationOfPFCReportingEmissions))
      .plus(inherentOfCO2ReportableEmissionsAmount(emissions?.INHERENT_CO2 as InherentCO2ReportingEmissions))
      .plus(fallbackReportableEmissionsAmount(emissions?.FALLBACK as FallbackReportingEmissions, emissionType)),
  );
}

export function calculateTotalSustainableBiomassAmount(emissions: {
  [key: string]: DreMonitoringApproachReportingEmissions;
}): string {
  const emissionType: emissionTypeKey = 'sustainableBiomass';
  return format(
    calculationOfCO2ReportableEmissionsAmount(
      emissions?.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions,
      emissionType,
    )
      .plus(
        measurementOfCO2ReportableEmissionsAmount(
          emissions?.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions,
          emissionType,
        ),
      )
      .plus(
        measurementOfN2OReportableEmissionsAmount(
          emissions?.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions,
          emissionType,
        ),
      )
      .plus(fallbackReportableEmissionsAmount(emissions?.FALLBACK as FallbackReportingEmissions, emissionType)),
  );
}

function calculationOfCO2ReportableEmissionsAmount(
  emissions: CalculationOfCO2ReportingEmissions,
  emissionTypeKey: emissionTypeKey,
): BigNumber {
  return valueIfPresentOrZero(emissions?.combustionEmissions[emissionTypeKey])
    .plus(valueIfPresentOrZero(emissions?.processEmissions[emissionTypeKey]))
    .plus(valueIfPresentOrZero(emissions?.massBalanceEmissions[emissionTypeKey]))
    .plus(valueIfPresentOrZero(emissions?.transferredCO2Emissions?.[emissionTypeKey]));
}

function measurementOfCO2ReportableEmissionsAmount(
  emissions: MeasurementOfCO2ReportingEmissions,
  emissionTypeKey: emissionTypeKey,
): BigNumber {
  return valueIfPresentOrZero(emissions?.emissions[emissionTypeKey]).plus(
    valueIfPresentOrZero(emissions?.transferredCO2Emissions?.[emissionTypeKey]),
  );
}

function measurementOfN2OReportableEmissionsAmount(
  emissions: MeasurementOfN2OReportingEmissions,
  emissionTypeKey: emissionTypeKey,
): BigNumber {
  return valueIfPresentOrZero(emissions?.emissions[emissionTypeKey]).plus(
    valueIfPresentOrZero(emissions?.transferredN2OEmissions?.[emissionTypeKey]),
  );
}

function calculationOfPFCReportableEmissionsAmount(emissions: CalculationOfPFCReportingEmissions): BigNumber {
  return valueIfPresentOrZero(emissions?.totalEmissions?.reportableEmissions);
}

function inherentOfCO2ReportableEmissionsAmount(emissions: InherentCO2ReportingEmissions): BigNumber {
  return valueIfPresentOrZero(emissions?.totalEmissions?.reportableEmissions);
}

function fallbackReportableEmissionsAmount(
  emissions: FallbackReportingEmissions,
  emissionTypeKey: emissionTypeKey,
): BigNumber {
  return valueIfPresentOrZero(emissions?.emissions[emissionTypeKey]);
}

function valueIfPresentOrZero(value: BigNumber.Value) {
  return new BigNumber(value || 0);
}
