import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { isCalculationWizardComplete } from '@shared/components/approaches/aer/monitoring-approaches.functions';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { statusMap } from '@shared/task-list/task-item/status.map';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import BigNumber from 'bignumber.js';

import { GovukTableColumn } from 'govuk-components';

import { Aer, CalculationOfCO2Emissions } from 'pmrv-api';

interface TemplateCalculationSourceStreamEmission {
  sourceStreamDescription?: string;
  emissionSources?: string[];
  reportableEmissions?: string;
  biomassEmissions?: string;
  status?: string;
}

@Component({
  selector: 'app-calculation-emissions-group',
  templateUrl: './calculation-emissions-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationEmissionsGroupComponent implements OnInit {
  @Input() data: Aer;
  @Input() statuses: TaskItemStatus[];
  @Input() isReview = true;

  sourceStreamEmissions: TemplateCalculationSourceStreamEmission[];

  statusMap = statusMap;

  columns: GovukTableColumn<TemplateCalculationSourceStreamEmission>[] = [
    { field: 'sourceStreamDescription', header: 'Source stream' },
    { field: 'emissionSources', header: 'Emission sources' },
    { field: 'reportableEmissions', header: 'Reportable emissions' },
    { field: 'biomassEmissions', header: 'Sustainable biomass' },
    { field: 'status', header: '' },
  ];

  ngOnInit(): void {
    const monitoringApproachEmissions = this.data.monitoringApproachEmissions;

    const hasTransfer = !!(monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.hasTransfer;

    const currentSourceStreamEmissions = (monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
      ?.sourceStreamEmissions;
    const aerSourceStreams = this.data.sourceStreams;
    const aerEmissionSources = this.data.emissionSources;

    this.sourceStreamEmissions = [...(currentSourceStreamEmissions ?? [])]?.map((sourceStreamEmission, index) => {
      const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();
      const aerSourceStream = aerSourceStreams.find(
        (sourceStream) => sourceStream.id === sourceStreamEmission.sourceStream,
      );

      const sourceStreamDescription = aerSourceStream?.reference
        ? `${aerSourceStream.reference} ${sourceStreamDescriptionPipe.transform(aerSourceStream.description)}`
        : null;

      const emissionSources = sourceStreamEmission.emissionSources?.map(
        (currentEmissionSource) =>
          aerEmissionSources.find((emissionSource) => emissionSource.id === currentEmissionSource)?.reference,
      );

      let [reportableEmissions, biomassEmissions] =
        this.isReview || isCalculationWizardComplete(sourceStreamEmission, hasTransfer)
          ? this.getEmissionsValues(sourceStreamEmission)
          : [0, 0];

      if (hasTransfer && !!sourceStreamEmission?.transfer?.entryAccountingForTransfer) {
        if (sourceStreamEmission?.transfer?.transferDirection === 'RECEIVED_FROM_ANOTHER_INSTALLATION') {
          reportableEmissions = Math.abs(reportableEmissions);
          biomassEmissions = Math.abs(biomassEmissions);
        } else {
          reportableEmissions = -Math.abs(reportableEmissions);
          biomassEmissions = -Math.abs(biomassEmissions);
        }
      }

      return {
        sourceStreamDescription,
        emissionSources,
        reportableEmissions: reportableEmissions,
        biomassEmissions: biomassEmissions,
        status: this.isReview ? '' : this.statuses[index],
      };
    });

    if (this.sourceStreamEmissions.length) {
      let sumOfReportableEmissions = new BigNumber(0),
        sumOfBiomassEmissions = new BigNumber(0);

      this.sourceStreamEmissions.forEach((sourceStreamEmission) => {
        const bigReportableEmissions = new BigNumber(sourceStreamEmission.reportableEmissions);
        const bigBiomassEmissions = new BigNumber(sourceStreamEmission.biomassEmissions);
        sumOfReportableEmissions = bigReportableEmissions.plus(sumOfReportableEmissions);
        sumOfBiomassEmissions = bigBiomassEmissions.plus(sumOfBiomassEmissions);
      });

      this.sourceStreamEmissions.push({
        sourceStreamDescription: 'totals',
        emissionSources: [''],
        reportableEmissions: sumOfReportableEmissions.toString(),
        biomassEmissions: sumOfBiomassEmissions.toString(),
        status: '',
      });
    }
  }

  private getEmissionsValues(sourceStreamEmission) {
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

    return [
      totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0,
      totalProvidedSustainableBiomassEmissions ?? totalSustainableBiomassEmissions ?? 0,
    ];
  }
}
