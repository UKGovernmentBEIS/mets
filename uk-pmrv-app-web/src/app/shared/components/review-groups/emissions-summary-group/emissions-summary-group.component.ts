import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  getFallbackSummaries,
  getFallbackSummariesTotals,
} from '@shared/components/review-groups/emissions-summary-group/approaches/emissions-summary-fallback';
import { getInherentCO2Summaries } from '@shared/components/review-groups/emissions-summary-group/approaches/emissions-summary-inherent-co2';
import { TemplateEmissionsSummary } from '@shared/components/review-groups/emissions-summary-group/interfaces/template-emissions-summary.interface';
import BigNumber from 'bignumber.js';

import { GovukTableColumn } from 'govuk-components';

import {
  Aer,
  AerInherentReceivingTransferringInstallation,
  CalculationOfCO2Emissions,
  CalculationOfPfcEmissions,
  CalculationSourceStreamEmission,
  FallbackEmissions,
  InherentCO2Emissions,
  MeasurementCO2EmissionPointEmission,
  MeasurementN2OEmissionPointEmission,
  MeasurementOfCO2Emissions,
  MeasurementOfN2OEmissions,
  PfcSourceStreamEmission,
  SourceStream,
} from 'pmrv-api';

import {
  getCalculationSummaries,
  getCalculationTransferredSummaries,
} from './approaches/emissions-summary-calculations';
import {
  getMeasurementSummaries,
  getMeasurementTransferredSummaries,
} from './approaches/emissions-summary-measurement';
import { getPfcSummaries, getPfcSummariesTotals } from './approaches/emissions-summary-pfc';

@Component({
  selector: 'app-emissions-summary-group',
  templateUrl: './emissions-summary-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryGroupComponent implements OnInit {
  @Input() data: Aer;

  DECIMAL_DIGITS: number = 0;

  sourceStreams: Array<SourceStream>;

  calculationsHasTransfer: boolean;
  measurementCO2HasTransfer: boolean;
  measurementN2OHasTransfer: boolean;

  calculationEmissions: Array<CalculationSourceStreamEmission>;
  measurementCO2Emissions: Array<MeasurementCO2EmissionPointEmission>;
  pfcEmissions: Array<PfcSourceStreamEmission>;
  inherentCO2Emissions: Array<AerInherentReceivingTransferringInstallation>;
  measurementN2OEmissions: Array<MeasurementN2OEmissionPointEmission>;
  fallbackEmissions: FallbackEmissions;

  emissionsSummary: TemplateEmissionsSummary[];

  columns: GovukTableColumn<TemplateEmissionsSummary>[] = [
    { field: 'approaches', header: 'Approaches' },
    { field: 'totalReportableEmissions', header: 'Reportable emissions' },
    { field: 'totalBiomassEmissions', header: 'Sustainable biomass' },
  ];

  ngOnInit(): void {
    const calculationCO2 = this.data?.monitoringApproachEmissions?.CALCULATION_CO2 as CalculationOfCO2Emissions;
    const measurementCO2 = this.data?.monitoringApproachEmissions?.MEASUREMENT_CO2 as MeasurementOfCO2Emissions;
    const measurementN2O = this.data?.monitoringApproachEmissions?.MEASUREMENT_N2O as MeasurementOfN2OEmissions;
    const pfc = this.data?.monitoringApproachEmissions?.CALCULATION_PFC as CalculationOfPfcEmissions;
    const inherentCO2 = this.data?.monitoringApproachEmissions?.INHERENT_CO2 as InherentCO2Emissions;
    const fallback = this.data?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;

    this.sourceStreams = this.data?.sourceStreams;

    this.calculationEmissions = calculationCO2?.sourceStreamEmissions;
    this.calculationsHasTransfer = !!calculationCO2?.hasTransfer;

    this.measurementCO2Emissions = measurementCO2?.emissionPointEmissions;
    this.measurementCO2HasTransfer = !!measurementCO2?.hasTransfer;

    this.measurementN2OEmissions = measurementN2O?.emissionPointEmissions;
    this.measurementN2OHasTransfer = !!measurementN2O?.hasTransfer;

    this.pfcEmissions = pfc?.sourceStreamEmissions;

    this.inherentCO2Emissions = inherentCO2?.inherentReceivingTransferringInstallations;

    this.fallbackEmissions = fallback;

    this.emissionsSummary = [
      ...(this.calculationEmissions
        ? getCalculationSummaries(this.sourceStreams, this.calculationEmissions, this.calculationsHasTransfer)
        : []),
      ...(this.calculationEmissions && this.calculationsHasTransfer
        ? getCalculationTransferredSummaries(this.calculationEmissions, this.calculationsHasTransfer)
        : []),

      ...(this.measurementCO2Emissions
        ? getMeasurementSummaries(this.measurementCO2Emissions, this.measurementCO2HasTransfer, 'MEASUREMENT_CO2')
        : []),
      ...(this.measurementCO2Emissions && this.measurementCO2HasTransfer
        ? getMeasurementTransferredSummaries(
            this.measurementCO2Emissions,
            this.measurementCO2HasTransfer,
            'MEASUREMENT_CO2',
          )
        : []),

      ...(this.measurementN2OEmissions
        ? getMeasurementSummaries(this.measurementN2OEmissions, this.measurementN2OHasTransfer, 'MEASUREMENT_N2O')
        : []),
      ...(this.measurementN2OEmissions && this.measurementN2OHasTransfer
        ? getMeasurementTransferredSummaries(
            this.measurementN2OEmissions,
            this.measurementN2OHasTransfer,
            'MEASUREMENT_N2O',
          )
        : []),

      ...(this.pfcEmissions ? getPfcSummaries(this.pfcEmissions) : []),

      ...(this.fallbackEmissions ? getFallbackSummaries(this.fallbackEmissions) : []),

      ...this.getTotal(),

      ...(this.inherentCO2Emissions ? getInherentCO2Summaries(this.inherentCO2Emissions) : []),
    ];
  }

  getCalculationSummariesTotals() {
    let totalReportableEmissions = new BigNumber(0);
    let totalBiomassEmissions = new BigNumber(0);

    const calculationSummaries = getCalculationSummaries(
      this.sourceStreams,
      this.calculationEmissions,
      this.calculationsHasTransfer,
    );

    calculationSummaries.forEach((calculationSummary) => {
      const bigReportableEmissions = new BigNumber(calculationSummary.totalReportableEmissions);
      const bigBiomassEmissions = new BigNumber(calculationSummary.totalBiomassEmissions);
      totalReportableEmissions = bigReportableEmissions.plus(totalReportableEmissions);
      totalBiomassEmissions = bigBiomassEmissions.plus(totalBiomassEmissions);
    });

    if (this.calculationsHasTransfer) {
      const transferredSummaries = getCalculationTransferredSummaries(
        this.calculationEmissions,
        this.calculationsHasTransfer,
      );

      const bigTransferredReportableEmissions = new BigNumber(transferredSummaries[0].totalReportableEmissions);
      const bigTransferredBiomassEmissions = new BigNumber(transferredSummaries[0].totalBiomassEmissions);

      totalReportableEmissions = totalReportableEmissions.plus(bigTransferredReportableEmissions);
      totalBiomassEmissions = totalBiomassEmissions.plus(bigTransferredBiomassEmissions);
    }

    return [totalReportableEmissions, totalBiomassEmissions];
  }

  getMeasurementSummariesTotals(measurementType) {
    let totalReportableEmissions = new BigNumber(0);
    let totalBiomassEmissions = new BigNumber(0);

    const measurementEmissions =
      measurementType === 'MEASUREMENT_CO2' ? this.measurementCO2Emissions : this.measurementN2OEmissions;
    const measurementHasTransfer =
      measurementType === 'MEASUREMENT_CO2' ? this.measurementCO2HasTransfer : this.measurementN2OHasTransfer;

    const measurementSummaries = getMeasurementSummaries(measurementEmissions, measurementHasTransfer, measurementType);

    measurementSummaries.forEach((measurementCO2Summary) => {
      const bigReportableEmissions = new BigNumber(measurementCO2Summary.totalReportableEmissions);
      const bigBiomassEmissions = new BigNumber(measurementCO2Summary.totalBiomassEmissions);
      totalReportableEmissions = bigReportableEmissions.plus(totalReportableEmissions);
      totalBiomassEmissions = bigBiomassEmissions.plus(totalBiomassEmissions);
    });

    if (measurementHasTransfer) {
      const transferredSummaries = getMeasurementTransferredSummaries(
        measurementEmissions,
        measurementHasTransfer,
        measurementType,
      );

      const bigTransferredReportableEmissions = new BigNumber(transferredSummaries[0].totalReportableEmissions);
      const bigTransferredBiomassEmissions = new BigNumber(transferredSummaries[0].totalBiomassEmissions);

      totalReportableEmissions = totalReportableEmissions.plus(bigTransferredReportableEmissions);
      totalBiomassEmissions = totalBiomassEmissions.plus(bigTransferredBiomassEmissions);
    }

    return [totalReportableEmissions, totalBiomassEmissions];
  }

  getTotal(): TemplateEmissionsSummary[] {
    let totalReportableEmissions = new BigNumber(0);
    let totalBiomassEmissions = new BigNumber(0);

    const [calculationsTotalReportableEmissions, calculationsTotalBiomassEmissions] = this.calculationEmissions
      ? this.getCalculationSummariesTotals()
      : [0, 0];

    const [measurementCO2TotalReportableEmissions, measurementCO2TotalBiomassEmissions] = this.measurementCO2Emissions
      ? this.getMeasurementSummariesTotals('MEASUREMENT_CO2')
      : [0, 0];

    const [measurementN2OTotalReportableEmissions, measurementN2OTotalBiomassEmissions] = this.measurementN2OEmissions
      ? this.getMeasurementSummariesTotals('MEASUREMENT_N2O')
      : [0, 0];

    const pfcTotalReportableEmissions = this.pfcEmissions ? getPfcSummariesTotals(this.pfcEmissions) : 0;

    const [fallbackTotalReportableEmissions, fallbackTotalBiomassEmissions] = this.fallbackEmissions
      ? getFallbackSummariesTotals(this.fallbackEmissions)
      : [0, 0];

    totalReportableEmissions = totalReportableEmissions
      .plus(calculationsTotalReportableEmissions)
      .plus(measurementCO2TotalReportableEmissions)
      .plus(measurementN2OTotalReportableEmissions)
      .plus(fallbackTotalReportableEmissions)
      .plus(pfcTotalReportableEmissions);

    totalBiomassEmissions = totalBiomassEmissions
      .plus(calculationsTotalBiomassEmissions)
      .plus(measurementCO2TotalBiomassEmissions)
      .plus(measurementN2OTotalBiomassEmissions)
      .plus(fallbackTotalBiomassEmissions);

    return [
      {
        approaches: 'Total',
        totalReportableEmissions: totalReportableEmissions.toString(),
        totalBiomassEmissions: totalBiomassEmissions.toString(),
      },
    ];
  }
}
