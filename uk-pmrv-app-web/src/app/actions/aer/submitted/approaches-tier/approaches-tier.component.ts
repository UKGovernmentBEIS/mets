import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-approaches-tier',
  templateUrl: './approaches-tier.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesTierComponent {
  taskKey = this.route.snapshot.data.taskKey;
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
  >;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  heading$ = combineLatest([this.payload$, this.index$]).pipe(
    first(),
    map(([payload, index]) => {
      const sourceStreamEmission = (payload.aer.monitoringApproachEmissions[this.taskKey] as any).sourceStreamEmissions[
        index
      ];
      const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();
      const aerSourceStream = payload.aer.sourceStreams.find(
        (sourceStream) => sourceStream.id === sourceStreamEmission.sourceStream,
      );

      return `${aerSourceStream.reference} ${sourceStreamDescriptionPipe.transform(aerSourceStream.description)}`;
    }),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
