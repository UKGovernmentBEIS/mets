import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { addDays, startOfDay } from 'date-fns';

import { PermitNotificationFollowUpExtendDateRequestTaskActionPayload } from 'pmrv-api';

import { PermitNotificationService } from '../../../../core/permit-notification.service';
import {
  PERMIT_NOTIFICATION_FOLLOW_UP_FORM,
  permitNotificationFollowUpFormProvider,
} from '../../../factory/form-provider';
import {
  FOLLOW_UP_REVIEW_DECISION_FORM,
  followUpReviewDecisionFormProvider,
} from '../../../review/decision/decision-form.provider';

@Component({
  selector: 'app-due-date',
  templateUrl: './due-date.component.html',
  providers: [permitNotificationFollowUpFormProvider, followUpReviewDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DueDateComponent implements OnInit {
  isEditable$ = this.store.select('isEditable');
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  minDate: Date;
  taskType$ = this.store.requestTaskType$;

  constructor(
    @Inject(PERMIT_NOTIFICATION_FOLLOW_UP_FORM) readonly form: UntypedFormGroup,
    @Inject(FOLLOW_UP_REVIEW_DECISION_FORM) readonly decisionForm: UntypedFormGroup,
    private readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly permitNotificationService: PermitNotificationService,
    private readonly router: Router,
  ) {
    this.minDate = startOfDay(addDays(new Date(), 1));
  }

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    if (this.form.valid) {
      const payload: PermitNotificationFollowUpExtendDateRequestTaskActionPayload = {
        dueDate: this.form.get('followUpResponseExpirationDate').value,
      };

      this.permitNotificationService
        .postSubmit(
          'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE',
          'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD',
          payload,
        )
        .pipe(
          first(),
          switchMap(() => this.store.updateTimelineActions(this.store.requestId)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }

  onAmendsSubmit() {
    if (this.decisionForm.valid) {
      const payload: PermitNotificationFollowUpExtendDateRequestTaskActionPayload = {
        dueDate: this.decisionForm.get('dueDate').value,
      };

      this.permitNotificationService
        .postSubmit(
          'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE',
          'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD',
          payload,
        )
        .pipe(
          first(),
          switchMap(() => this.store.updateTimelineActions(this.store.requestId)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.store.setState({
            ...this.store.getState(),
            requestTaskItem: {
              ...this.store.getState().requestTaskItem,
              requestTask: {
                ...this.store.getState().requestTaskItem.requestTask,
                payload: {
                  ...this.store.getState().requestTaskItem.requestTask.payload,
                  reviewDecision: {
                    ...(this.store.getState().requestTaskItem.requestTask.payload as any).reviewDecision,
                    details: {
                      ...(this.store.getState().requestTaskItem.requestTask.payload as any).reviewDecision?.details,
                      dueDate: payload.dueDate,
                    },
                  },
                } as any,
              },
            },
          });
          this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } });
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }
}
