import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAlrDataRegulatorReviewDecision, ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import {
  ALR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM,
  alrVerificationReviewGroupDecisionFormProvider,
} from './alr-verification-review-group-decision-form.provider';

@Component({
  selector: 'app-alr-verification-review-group-decision',
  templateUrl: './alr-verification-review-group-decision.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule],
  providers: [alrVerificationReviewGroupDecisionFormProvider],
})
export class AlrVerificationReviewGroupDecisionComponent implements PendingRequest {
  alrPayload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.alrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as ALRAlrDataRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.alrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  isEditable$ = this.alrService.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(ALR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly alrService: AlrService,
    readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.alrService.postGroupDecisionReview(
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
