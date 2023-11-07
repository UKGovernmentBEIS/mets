import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
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
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave(undefined, undefined, true, 'FALLBACK')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
