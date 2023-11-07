import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import BigNumber from 'bignumber.js';

import { GovukTableColumn } from 'govuk-components';

import {
  CalculationOfCO2ReportingEmissions,
  CalculationOfPFCReportingEmissions,
  DreMonitoringApproachReportingEmissions,
  FallbackReportingEmissions,
  InherentCO2ReportingEmissions,
  MeasurementOfCO2ReportingEmissions,
  MeasurementOfN2OReportingEmissions,
} from 'pmrv-api';

import { format } from '../../../../shared/utils/bignumber.utils';
import {
  calculateTotalReportableEmissionsAmount,
  calculateTotalSustainableBiomassAmount,
} from '../../../../tasks/dre/core/reportable.emissions';

interface EmissionRow {
  label: string;
  reportableEmissions: string;
  sustainableBiomass: string;
}

@Component({
  selector: 'app-reportable-emissions-summary-template',
  templateUrl: './reportable-emissions-summary-template.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportableEmissionsSummaryTemplateComponent implements OnInit {
  @Input() approachEmissions: {
    [key: string]: DreMonitoringApproachReportingEmissions;
  };
  @Input() editable: boolean;

  emissions: EmissionRow[] = [];

  columns: GovukTableColumn[] = [
    { field: 'label', header: 'Approaches' },
    { field: 'reportableEmissions', header: 'Reportable emissions' },
    { field: 'sustainableBiomass', header: 'Sustainable biomass' },
    { field: 'editBtn', header: undefined },
  ];

  ngOnInit(): void {
    this.populateCalculationOfCO2Emissions();
    this.populateMeasurementOfCO2Emissions();
    this.populateMeasurementOfN2OEmissions();
    this.populateCalculationOfPFCEmissions();
    this.populateInherentCO2Emissions();
    this.populateFallbackEmissions();
    this.populateTotalEmissions();
  }

  private populateCalculationOfCO2Emissions() {
    if (this.approachEmissions.CALCULATION_CO2) {
      const approach = this.approachEmissions.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions;
      this.emissions.push({
        label: 'Calculation of combustion',
        reportableEmissions: format(new BigNumber(approach.combustionEmissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.combustionEmissions.sustainableBiomass)),
      });

      this.emissions.push({
        label: 'Calculation of process',
        reportableEmissions: format(new BigNumber(approach.processEmissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.processEmissions.sustainableBiomass)),
      });

      this.emissions.push({
        label: 'Calculation of mass balance',
        reportableEmissions: format(new BigNumber(approach.massBalanceEmissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.massBalanceEmissions.sustainableBiomass)),
      });

      if (approach.calculateTransferredCO2) {
        this.emissions.push({
          label: 'Calculation of transferred CO2',
          reportableEmissions: format(new BigNumber(approach.transferredCO2Emissions.reportableEmissions)),
          sustainableBiomass: format(new BigNumber(approach.transferredCO2Emissions.sustainableBiomass)),
        });
      }
    }
  }

  private populateMeasurementOfCO2Emissions() {
    if (this.approachEmissions.MEASUREMENT_CO2) {
      const approach = this.approachEmissions.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions;
      this.emissions.push({
        label: 'Measurement of CO2',
        reportableEmissions: format(new BigNumber(approach.emissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.emissions.sustainableBiomass)),
      });

      if (approach.measureTransferredCO2) {
        this.emissions.push({
          label: 'Measurement of transferred CO2',
          reportableEmissions: format(new BigNumber(approach.transferredCO2Emissions.reportableEmissions)),
          sustainableBiomass: format(new BigNumber(approach.transferredCO2Emissions.sustainableBiomass)),
        });
      }
    }
  }

  private populateMeasurementOfN2OEmissions() {
    if (this.approachEmissions.MEASUREMENT_N2O) {
      const approach = this.approachEmissions.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions;
      this.emissions.push({
        label: 'Measurement of N2O',
        reportableEmissions: format(new BigNumber(approach.emissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.emissions.sustainableBiomass)),
      });

      if (approach.measureTransferredN2O) {
        this.emissions.push({
          label: 'Measurement of transferred N2O',
          reportableEmissions: format(new BigNumber(approach.transferredN2OEmissions.reportableEmissions)),
          sustainableBiomass: format(new BigNumber(approach.transferredN2OEmissions.sustainableBiomass)),
        });
      }
    }
  }

  private populateCalculationOfPFCEmissions() {
    if (this.approachEmissions.CALCULATION_PFC) {
      const approach = this.approachEmissions.CALCULATION_PFC as CalculationOfPFCReportingEmissions;
      this.emissions.push({
        label: 'Calculation of PFC',
        reportableEmissions: format(new BigNumber(approach.totalEmissions.reportableEmissions)),
        sustainableBiomass: '',
      });
    }
  }

  private populateInherentCO2Emissions() {
    if (this.approachEmissions.INHERENT_CO2) {
      const approach = this.approachEmissions.INHERENT_CO2 as InherentCO2ReportingEmissions;
      this.emissions.push({
        label: 'Inherent CO2',
        reportableEmissions: format(new BigNumber(approach.totalEmissions.reportableEmissions)),
        sustainableBiomass: '',
      });
    }
  }

  private populateFallbackEmissions() {
    if (this.approachEmissions.FALLBACK) {
      const approach = this.approachEmissions.FALLBACK as FallbackReportingEmissions;
      this.emissions.push({
        label: 'Fall-back',
        reportableEmissions: format(new BigNumber(approach.emissions.reportableEmissions)),
        sustainableBiomass: format(new BigNumber(approach.emissions.sustainableBiomass)),
      });
    }
  }

  private populateTotalEmissions() {
    this.emissions.push({
      label: 'Total',
      reportableEmissions: calculateTotalReportableEmissionsAmount(this.approachEmissions),
      sustainableBiomass: calculateTotalSustainableBiomassAmount(this.approachEmissions),
    });
  }
}
