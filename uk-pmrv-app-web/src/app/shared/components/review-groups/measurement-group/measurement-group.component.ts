import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { statusMap } from '@shared/task-list/task-item/status.map';
import BigNumber from 'bignumber.js';

import { GovukTableColumn } from 'govuk-components';

import { Aer } from 'pmrv-api';

import { SourceStreamDescriptionPipe } from '../../../pipes/source-streams-description.pipe';
import { TaskItemStatus } from '../../../task-list/task-list.interface';
import { isMeasurementWizardComplete } from '../../approaches/aer/monitoring-approaches.functions';

interface TemplateEmissionPointEmission {
  emissionPointDescription?: string;
  sourceStreams?: string[];
  emissionSources?: string[];
  reportableEmissions?: string;
  biomassEmissions?: string;
  status?: string;
}

@Component({
  selector: 'app-measurement-group',
  templateUrl: './measurement-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementGroupComponent implements OnInit {
  @Input() data: Aer;
  @Input() statuses: TaskItemStatus[];
  @Input() taskKey;
  @Input() isReview = true;

  emissionPointEmissions: TemplateEmissionPointEmission[];

  statusMap = statusMap;

  columns: GovukTableColumn<TemplateEmissionPointEmission>[] = [
    { field: 'emissionPointDescription', header: 'Emission point' },
    { field: 'sourceStreams', header: 'Source streams' },
    { field: 'emissionSources', header: 'Emission sources' },
    { field: 'reportableEmissions', header: 'Reportable emissions' },
    { field: 'biomassEmissions', header: 'Sustainable biomass' },
    { field: 'status', header: '' },
  ];

  ngOnInit(): void {
    const monitoringApproachEmissions = this.data.monitoringApproachEmissions;

    const hasTransfer = !!(monitoringApproachEmissions[this.taskKey] as any)?.hasTransfer;

    const currentΕmissionPointEmissions = (monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions;

    const aerEmissionPoints = this.data.emissionPoints;
    const aerSourceStreams = this.data.sourceStreams;
    const aerEmissionSources = this.data.emissionSources;

    this.emissionPointEmissions = [...(currentΕmissionPointEmissions ?? [])]?.map((emissionPointEmission, index) => {
      const emissionPointDescription = aerEmissionPoints.find(
        (aerEmissionPoint) => aerEmissionPoint.id === emissionPointEmission.emissionPoint,
      )?.reference;

      const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();

      const sourceStreams = [];
      emissionPointEmission.sourceStreams?.forEach((currentSourceStream) => {
        const result = aerSourceStreams.find((sourceStream) => sourceStream.id === currentSourceStream);

        if (result?.reference) {
          sourceStreams.push(result.reference + ' ' + sourceStreamDescriptionPipe.transform(result.description));
        }
      });

      const emissionSources = [];
      emissionPointEmission.emissionSources?.forEach((currentEmissionSource) => {
        const result = aerEmissionSources.find(
          (emissionSource) => emissionSource.id === currentEmissionSource,
        )?.reference;

        if (result) {
          emissionSources.push(result);
        }
      });

      let [reportableEmissions, biomassEmissions] =
        this.isReview || isMeasurementWizardComplete(emissionPointEmission, hasTransfer)
          ? this.getEmissionsValues(emissionPointEmission)
          : [0, 0];

      if (hasTransfer && !!emissionPointEmission?.transfer?.entryAccountingForTransfer) {
        if ((emissionPointEmission?.transfer as any)?.transferDirection === 'RECEIVED_FROM_ANOTHER_INSTALLATION') {
          reportableEmissions = Math.abs(reportableEmissions);
          biomassEmissions = Math.abs(biomassEmissions);
        } else {
          reportableEmissions = -Math.abs(reportableEmissions);
          biomassEmissions = -Math.abs(biomassEmissions);
        }
      }

      return {
        emissionPointDescription,
        sourceStreams,
        emissionSources,
        reportableEmissions: reportableEmissions,
        biomassEmissions: biomassEmissions,
        status: this.isReview ? '' : this.statuses[index],
      };
    });

    if (this.emissionPointEmissions.length) {
      let sumOfReportableEmissions = new BigNumber(0),
        sumOfBiomassEmissions = new BigNumber(0);

      this.emissionPointEmissions.forEach((emissionPointEmission) => {
        const bigReportableEmissions = new BigNumber(emissionPointEmission.reportableEmissions);
        const bigBiomassEmissions = new BigNumber(emissionPointEmission.biomassEmissions);
        sumOfReportableEmissions = bigReportableEmissions.plus(sumOfReportableEmissions);
        sumOfBiomassEmissions = bigBiomassEmissions.plus(sumOfBiomassEmissions);
      });

      this.emissionPointEmissions.push({
        emissionPointDescription: 'totals',
        emissionSources: [''],
        reportableEmissions: sumOfReportableEmissions.toString(),
        biomassEmissions: sumOfBiomassEmissions.toString(),
        status: '',
      });
    }
  }

  private getEmissionsValues(emissionPointEmission) {
    const totalProvidedReportableEmissions = emissionPointEmission?.providedEmissions?.totalProvidedReportableEmissions;

    const totalProvidedSustainableBiomassEmissions =
      emissionPointEmission?.providedEmissions?.totalProvidedSustainableBiomassEmissions;

    const totalReportableEmissions = emissionPointEmission?.reportableEmissions
      ? emissionPointEmission.reportableEmissions
      : 0;

    const totalSustainableBiomassEmissions = emissionPointEmission?.sustainableBiomassEmissions
      ? emissionPointEmission?.sustainableBiomassEmissions
      : 0;

    return [
      totalProvidedReportableEmissions ?? totalReportableEmissions ?? 0,
      totalProvidedSustainableBiomassEmissions ?? totalSustainableBiomassEmissions ?? 0,
    ];
  }
}
