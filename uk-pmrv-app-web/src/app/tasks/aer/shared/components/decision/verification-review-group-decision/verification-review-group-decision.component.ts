import { ChangeDetectionStrategy, Component, EventEmitter, Inject, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import {
  VERIFICATION_REVIEW_GROUP_DECISION_FORM,
  verificationReviewGroupDecisionFormProvider,
} from '@tasks/aer/shared/components/decision/verification-review-group-decision/verification-review-group-decision-form.provider';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationReviewRequestTaskPayload, AerVerificationReportDataReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-verification-review-group-decision',
  templateUrl: './verification-review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [verificationReviewGroupDecisionFormProvider],
})
export class VerificationReviewGroupDecisionComponent implements PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();

  decisionData$ = combineLatest([this.route.data, this.aerService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as AerApplicationReviewRequestTaskPayload)?.reviewGroupDecisions[
          data.groupKey
        ] as AerVerificationReportDataReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.aerService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as AerApplicationReviewRequestTaskPayload)?.reviewSectionsCompleted[data.groupKey] ?? true,
    ),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  isEditable$ = this.aerService.isEditable$;
  isOnEditState = false;

  constructor(
    @Inject(VERIFICATION_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: CommonTasksStore,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.aerService.postGroupDecisionReview(
              {
                type: this.form.controls.decision.value,
                details: {
                  notes: this.form.controls.notes.value,
                },
              },
              'VERIFICATION_REPORT_DATA',
              data.groupKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.isOnEditState = false;
          this.notification.emit(true);
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
