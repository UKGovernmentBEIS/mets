import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationSubmittedRequestActionPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  CalculationOfPfcEmissions,
  EmissionSource,
  PfcSourceStreamEmission,
  ReportingDataService,
  SourceStream,
} from 'pmrv-api';

@Component({
  selector: 'app-pfc-tier-summary',
  templateUrl: './pfc-tier-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PfcTierSummaryComponent implements OnInit {
  @Input() isEditable = false;
  @Input() payload:
    | AerApplicationSubmitRequestTaskPayload
    | AerApplicationReviewRequestTaskPayload
    | AerApplicationVerificationSubmitRequestTaskPayload
    | AerApplicationSubmittedRequestActionPayload;
  @Input() index: number;

  sourceStreamEmission: PfcSourceStreamEmission;
  sourceStream: SourceStream;
  emissionSources: EmissionSource[];
  permitParamMonitoringTiers: any;

  constructor(private readonly reportingDataService: ReportingDataService) {}

  ngOnInit(): void {
    this.sourceStreamEmission = (
      this.payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions
    )?.sourceStreamEmissions?.[this.index];

    this.sourceStream = this.payload.aer.sourceStreams.find(
      (sourceStream) => sourceStream.id === this.sourceStreamEmission?.sourceStream,
    );

    this.emissionSources = this.payload.aer.emissionSources.filter(
      (emissionSource) => this.sourceStreamEmission?.emissionSources?.includes(emissionSource.id) ?? false,
    );

    const sourceStreamEmissionId = this.sourceStreamEmission?.id;

    this.permitParamMonitoringTiers = sourceStreamEmissionId
      ? this.payload.permitOriginatedData?.permitMonitoringApproachMonitoringTiers
          ?.calculationPfcSourceStreamParamMonitoringTiers?.[sourceStreamEmissionId]
      : [];
  }
}
