import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  CalculationOfCO2ReportingEmissions,
  DreMonitoringApproachReportingEmissions,
  MeasurementOfCO2ReportingEmissions,
  MeasurementOfN2OReportingEmissions,
} from 'pmrv-api';

import { MonitoringApproachDescriptionPipe } from '../../../../shared/pipes/monitoring-approach-description.pipe';

@Component({
  selector: 'app-monitoring-approaches-summary-template',
  templateUrl: './monitoring-approaches-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [MonitoringApproachDescriptionPipe],
})
export class MonitoringApproachesSummaryTemplateComponent {
  @Input() approachEmissions: {
    [key: string]: DreMonitoringApproachReportingEmissions;
  };
  @Input() editable: boolean;

  constructor(private readonly monitoringApproachDescriptionPipe: MonitoringApproachDescriptionPipe) {}

  get selectedMonitoringApproaches(): string[] {
    const approaches = [];
    if (this.approachEmissions.CALCULATION_CO2) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('CALCULATION_CO2'));
      if ((this.approachEmissions.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions).calculateTransferredCO2) {
        approaches.push('Calculation of transferred CO2');
      }
    }

    if (this.approachEmissions.MEASUREMENT_CO2) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('MEASUREMENT_CO2'));
      if ((this.approachEmissions.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions).measureTransferredCO2) {
        approaches.push('Measurement of transferred CO2');
      }
    }

    if (this.approachEmissions.MEASUREMENT_N2O) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('MEASUREMENT_N2O'));
      if ((this.approachEmissions.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions).measureTransferredN2O) {
        approaches.push('Measurement of transferred N2O');
      }
    }

    if (this.approachEmissions.CALCULATION_PFC) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('CALCULATION_PFC'));
    }

    if (this.approachEmissions.INHERENT_CO2) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('INHERENT_CO2'));
    }

    if (this.approachEmissions.FALLBACK) {
      approaches.push(this.monitoringApproachDescriptionPipe.transform('FALLBACK'));
    }

    return approaches;
  }
}
