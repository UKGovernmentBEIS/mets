import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { AerApplicationSubmittedRequestActionPayload, FallbackEmissions } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { getFallbackSourceStreams } from './fallback';

@Component({
  selector: 'app-fallback',
  template: `
    <app-action-task header="{{ 'FALLBACK' | monitoringApproachEmissionDescription }}" [breadcrumb]="true">
      <app-fallback-emissions-group
        [fallbackEmissions]="fallbackEmissions$ | async"
        [sourceStreams]="sourceStreams$ | async"
        [documentFiles]="documentFiles$ | async"></app-fallback-emissions-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerPayload$ = this.aerService.getPayload() as Observable<AerApplicationSubmittedRequestActionPayload>;
  fallbackEmissions$ = this.aerPayload$.pipe(
    map((payload) => payload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions),
  );
  documentFiles$ = this.fallbackEmissions$.pipe(
    map((emissions) => emissions?.files),
    map((files) => (files ? this.aerService.getDownloadUrlFiles(files) : [])),
  );
  sourceStreams$ = combineLatest([this.aerPayload$, this.fallbackEmissions$]).pipe(
    first(),
    map(([payload, fallbackEmissions]) => getFallbackSourceStreams(payload, fallbackEmissions)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private readonly router: Router,
  ) {}
}
