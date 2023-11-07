import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload, AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-approaches-tier-review-summary',
  templateUrl: './approaches-tier-review-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesTierReviewSummaryComponent {
  groupKey = this.route.snapshot.data.groupKey;
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationReviewRequestTaskPayload | AerApplicationVerificationSubmitRequestTaskPayload
  >;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  heading$ = combineLatest([this.payload$, this.index$]).pipe(
    first(),
    map(([payload, index]) => {
      const sourceStreamEmission = (payload.aer.monitoringApproachEmissions[this.groupKey] as any)
        ?.sourceStreamEmissions[index];

      const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();
      const aerSourceStream = payload.aer.sourceStreams.find(
        (sourceStream) => sourceStream.id === sourceStreamEmission?.sourceStream,
      );

      return `${aerSourceStream.reference} ${sourceStreamDescriptionPipe.transform(aerSourceStream.description)}`;
    }),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
