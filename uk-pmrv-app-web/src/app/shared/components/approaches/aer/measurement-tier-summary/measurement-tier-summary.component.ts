import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationSubmittedRequestActionPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  EmissionPoint,
  EmissionSource,
  MeasurementCO2EmissionPointEmission,
  SourceStream,
} from 'pmrv-api';

@Component({
  selector: 'app-measurement-tier-summary',
  templateUrl: './measurement-tier-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementTierSummaryComponent implements OnInit {
  @Input() isEditable = false;
  @Input() taskKey: string;
  @Input() payload:
    | AerApplicationSubmitRequestTaskPayload
    | AerApplicationReviewRequestTaskPayload
    | AerApplicationVerificationSubmitRequestTaskPayload
    | AerApplicationSubmittedRequestActionPayload;
  @Input() index: number;

  emissionPointEmission: MeasurementCO2EmissionPointEmission;

  emissionPoint: EmissionPoint;
  sourceStreams: SourceStream[];
  emissionSources: EmissionSource[];
  permitParamMonitoringTiers: string;

  ngOnInit(): void {
    this.emissionPointEmission = (
      this.payload.aer.monitoringApproachEmissions[this.taskKey] as any
    )?.emissionPointEmissions?.[this.index];

    this.emissionPoint = this.payload.aer.emissionPoints.find(
      (emissionPoint) => emissionPoint.id === this.emissionPointEmission?.emissionPoint,
    );

    this.sourceStreams = this.payload.aer.sourceStreams.filter(
      (sourceStream) => this.emissionPointEmission?.sourceStreams?.includes(sourceStream.id) ?? false,
    );

    this.emissionSources = this.payload.aer.emissionSources.filter(
      (emissionSource) => this.emissionPointEmission?.emissionSources?.includes(emissionSource.id) ?? false,
    );

    const emissionPointEmissionId = this.emissionPointEmission?.id;

    this.permitParamMonitoringTiers = emissionPointEmissionId
      ? this.payload.permitOriginatedData?.permitMonitoringApproachMonitoringTiers
          ?.measurementCO2EmissionPointParamMonitoringTiers?.[emissionPointEmissionId]
      : '';
  }
}
