import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-fallback',
  template: `
    <app-page-heading>{{ 'FALLBACK' | monitoringApproachEmissionDescription }}</app-page-heading>
    <app-fallback-emissions-group
      [fallbackEmissions]="fallbackEmissions$ | async"
      [sourceStreams]="sourceStreams$ | async"
      [documentFiles]="documentFiles$ | async"></app-fallback-emissions-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent {
  aerPayload$ = this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>;
  fallbackEmissions$ = this.aerPayload$.pipe(
    map((payload) => payload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions),
  );
  documentFiles$ = this.fallbackEmissions$.pipe(
    map((emissions) => emissions?.files),
    map((files) => (files ? this.aerService.getDownloadUrlFiles(files) : [])),
  );
  sourceStreams$ = combineLatest([this.aerPayload$, this.fallbackEmissions$]).pipe(
    first(),
    map(([payload, fallbackEmissions]) => {
      return fallbackEmissions?.sourceStreams.map((sourceStreamKey) => {
        const aerSourceStream = payload.aer.sourceStreams.find((sourceStream) => sourceStream.id === sourceStreamKey);

        return `${aerSourceStream.reference} ${this.sourceStreamDescriptionPipe.transform(
          aerSourceStream.description,
        )}`;
      });
    }),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
  ) {}
}
