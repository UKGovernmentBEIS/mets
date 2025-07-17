import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { SummaryList } from '@permit-notification/follow-up/model/model';

import { PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload } from 'pmrv-api';

import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-action-notification-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationCompletedActionComponent {
  constructor(
    readonly route: ActivatedRoute,
    public readonly permitNotificationService: PermitNotificationService,
  ) {}

  decisionDetails$ = this.permitNotificationService.getPayload().pipe(
    first(),
    map((res) => res.reviewDecision),
  );

  followUpResponseDetailsSummaryListMapper: Record<
    keyof {
      request: string;
      responseExpirationDate: string;
      responseSubmissionDate: string;
      response: string;
      notes: string;
    },
    SummaryList
  > = {
    request: { label: 'Request from the regulator', order: 1, type: 'string' },
    responseExpirationDate: { label: 'Due date', order: 2, type: 'date' },
    responseSubmissionDate: { label: 'Submission date', order: 3, type: 'date' },
    response: { label: 'Operators response', order: 4, type: 'string' },
    notes: { label: 'Notes', order: 5, type: 'string' },
  };
  followUpResponseDetailsData$ = this.permitNotificationService.followUpResponseDetailsData$;
  followUpResponseDetailsFiles$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(map((payload) => this.permitNotificationService.getDownloadUrlFiles(payload.responseFiles)));

  usersInfo$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(map((payload) => payload?.usersInfo));

  signatory$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(map((payload) => payload?.reviewDecisionNotification?.signatory));

  operators$: Observable<string[]> = combineLatest([this.usersInfo$, this.signatory$]).pipe(
    map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
  );
}
