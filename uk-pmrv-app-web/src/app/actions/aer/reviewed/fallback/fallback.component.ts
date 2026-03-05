import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { AerApplicationCompletedRequestActionPayload, FallbackEmissions } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-fallback',
  template: `
    <app-action-task header="{{ 'FALLBACK' | monitoringApproachEmissionDescription }}" [breadcrumb]="true">
      <app-fallback-emissions-group
        [fallbackEmissions]="fallbackEmissions$ | async"
        [sourceStreams]="sourceStreams$ | async"
        [documentFiles]="documentFiles$ | async"></app-fallback-emissions-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent {
  aerPayload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
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
  decisionData$ = combineLatest([this.aerPayload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private readonly route: ActivatedRoute,
  ) {}
}
