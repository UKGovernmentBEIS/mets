import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import {
  BDRS2_REVIEW_VERIFICATION_REVIEW_DECISION_FORM,
  bdrs2VerificationReviewGroupDecisionFormProvider,
} from '@tasks/bdrs2/shared/components';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2Bdrs2DataRegulatorReviewDecision,
} from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-verification-review-group-decision',
  imports: [SharedModule],
  templateUrl: './bdrs2-verification-review-group-decision.component.html',
  providers: [bdrs2VerificationReviewGroupDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2VerificationReviewGroupDecisionComponent implements PendingRequest {
  bdrs2Payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.bdrs2Service.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as BDRS2Bdrs2DataRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.bdrs2Service.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  isEditable$ = this.bdrs2Service.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(BDRS2_REVIEW_VERIFICATION_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly bdrs2Service: BdrS2Service,
    readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.bdrs2Service.postGroupDecisionReview(
              this.constructReviewDecision(),
              'VERIFICATION_REPORT_DATA',
              data.groupKey,
              [],
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.router.navigate(['../'], { relativeTo: this.route });
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private constructReviewDecision(): any {
    return {
      type: this.form.controls.decision.value,
      details: {
        notes: this.form.controls.notes.value,
      },
    };
  }
}
