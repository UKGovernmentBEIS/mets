import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../../../core/aer.service';
import { getCalculationHeading, getMeasurementHeading } from '../../../approaches-tier/approaches-tier';
import { getFallbackSourceStreams } from '../../../fallback/fallback';
import { getInherentInstallations } from '../../../inherent-co2/inherent-co2';

@Component({
  selector: 'app-monitoring-approaches-report',
  templateUrl: './monitoring-approaches-report.component.html',
  styleUrl: './monitoring-approaches-report.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachesReportComponent {
  @Input() monitoringApproaches: any;
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();

  monitoringApproaches$ = this.payload$.pipe(
    filter((payload) => !!payload.aer.monitoringApproachEmissions),
    map((payload) => Object.keys(payload.aer.monitoringApproachEmissions)),
  );

  getCalculationHeading = getCalculationHeading;
  getMeasurementHeading = getMeasurementHeading;

  getFallbackSourceStreams = getFallbackSourceStreams;
  getInherentInstallations = getInherentInstallations;

  constructor(readonly aerService: AerService) {}
}
