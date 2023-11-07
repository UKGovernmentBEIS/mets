import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-fallback',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="{{ 'FALLBACK' | monitoringApproachEmissionDescription }}"
    >
      <app-fallback-emissions-group
        [fallbackEmissions]="fallbackEmissions$ | async"
        [sourceStreams]="sourceStreams$ | async"
        [documentFiles]="documentFiles$ | async"
      ></app-fallback-emissions-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerPayload$ = this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>;
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
    private readonly aerService: AerService,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private readonly router: Router,
  ) {}
}
