import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  reference = this.route.snapshot.paramMap.get('id');
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>;
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  operatorFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  regulatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.regulatorImprovementResponses[this.reference]),
  );
  regulatorFiles$ = this.regulatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getRegulatorDownloadUrlFiles(payload?.files) : [])),
  );
  operatorAirImprovementFollowUpResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementFollowUpResponses[this.reference]),
  );
  operatorFollowupFiles$ = this.operatorAirImprovementFollowUpResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) => {
          return this.airService.postAirRespondTaskSave({
            reference: Number(this.reference),
            operatorImprovementFollowUpResponse: payload?.operatorImprovementFollowUpResponses?.[this.reference],
            airRespondToRegulatorCommentsSectionsCompleted: {
              ...payload?.airRespondToRegulatorCommentsSectionsCompleted,
              [this.reference]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
