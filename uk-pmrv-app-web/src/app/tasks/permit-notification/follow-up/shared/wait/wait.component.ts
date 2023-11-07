import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import {
  PermitNotificationFollowUpWaitForAmendsRequestTaskPayload,
  PermitNotificationWaitForFollowUpRequestTaskPayload,
} from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { PermitNotificationService } from '../../../core/permit-notification.service';
import { SummaryList } from '../../model/model';

@Component({
  selector: 'app-wait',
  templateUrl: './wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitComponent {
  constructor(
    readonly route: ActivatedRoute,
    public readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }
  isEditable$ = this.store.select('isEditable');
  requestTaskType$ = this.store.requestTaskType$;
  notification: any;

  getPayload$: Observable<
    PermitNotificationWaitForFollowUpRequestTaskPayload | PermitNotificationFollowUpWaitForAmendsRequestTaskPayload
  > = this.permitNotificationService.getPayload();

  data$ = this.permitNotificationService.followUpData$;

  amendsData$ = this.permitNotificationService.amendsData$;
  amendsFiles$ = this.permitNotificationService.downloadUrlFiles$;

  amendsReviewDecisionData$ = this.permitNotificationService.getPayload().pipe(
    first(),
    map((res: PermitNotificationFollowUpWaitForAmendsRequestTaskPayload) => res.reviewDecision as any),
  );

  followUpSummaryListMapper: Record<
    keyof { followUpRequest: string; followUpResponseExpirationDate: string },
    SummaryList
  > = {
    followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
    followUpResponseExpirationDate: { label: 'Due date', order: 2, type: 'date', url: 'due-date' },
  };

  amendsSummaryListMapper: Record<keyof { followUpRequest: string; followUpResponse: string }, SummaryList> = {
    followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
    followUpResponse: { label: 'Operators response', order: 2, type: 'string' },
  };
}
