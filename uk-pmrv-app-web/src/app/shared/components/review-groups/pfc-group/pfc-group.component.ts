import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { isPfcWizardComplete } from '@shared/components/approaches/aer/monitoring-approaches.functions';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { statusMap } from '@shared/task-list/task-item/status.map';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import BigNumber from 'bignumber.js';

import { GovukTableColumn } from 'govuk-components';

import { Aer, CalculationOfPfcEmissions, PfcSourceStreamEmissionCalculationMethodData } from 'pmrv-api';

interface TemplateCalculationSourceStreamEmission {
  sourceStreamDescription?: string;
  emissionSources?: string[];
  calculationMethod?: PfcSourceStreamEmissionCalculationMethodData['calculationMethod'] | '';
  reportableEmissions?: string;
  status?: string;
}

@Component({
  selector: 'app-pfc-group',
  templateUrl: './pfc-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PfcGroupComponent implements OnInit {
  @Input() data: Aer;
  @Input() statuses: TaskItemStatus[];
  @Input() isReview = true;

  sourceStreamEmissions: TemplateCalculationSourceStreamEmission[];

  statusMap = statusMap;

  columns: GovukTableColumn<TemplateCalculationSourceStreamEmission>[] = [
    { field: 'sourceStreamDescription', header: 'Source stream' },
    { field: 'emissionSources', header: 'Emission sources' },
    { field: 'calculationMethod', header: 'Methodology' },
    { field: 'reportableEmissions', header: 'Reportable emissions' },
    { field: 'status', header: '' },
  ];

  ngOnInit(): void {
    const monitoringApproachEmissions = this.data.monitoringApproachEmissions;

    const currentSourceStreamEmissions = (monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)
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

      const reportableEmissions =
        this.isReview || isPfcWizardComplete(sourceStreamEmission) ? this.getEmissionsValues(sourceStreamEmission) : 0;

      return {
        sourceStreamDescription,
        emissionSources,
        calculationMethod: sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod,
        reportableEmissions: reportableEmissions,
        status: this.isReview ? '' : this.statuses[index],
      };
    });

    if (this.sourceStreamEmissions.length) {
      let sumOfReportableEmissions = new BigNumber(0);

      this.sourceStreamEmissions.forEach((sourceStreamEmission) => {
        const bigReportableEmissions = new BigNumber(sourceStreamEmission.reportableEmissions);

        sumOfReportableEmissions = bigReportableEmissions.plus(sumOfReportableEmissions);
      });

      this.sourceStreamEmissions.push({
        sourceStreamDescription: 'totals',
        emissionSources: [''],
        calculationMethod: '',
        reportableEmissions: sumOfReportableEmissions.toString(),
        status: '',
      });
    }
  }

  private getEmissionsValues(emissionPointEmission) {
    const totalProvidedReportableEmissions = emissionPointEmission?.providedEmissions?.totalProvidedReportableEmissions;

    const totalReportableEmissions = emissionPointEmission?.reportableEmissions
      ? emissionPointEmission.reportableEmissions
      : 0;

    return totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0;
  }
}
