import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import {
  BDR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM,
  bdrVerificationReviewGroupDecisionFormProvider,
} from '@tasks/bdr/shared/components/decision/bdr-review-decision/bdr-verification-review-group-decision-form.provider';
import { BdrService } from '@tasks/bdr/shared/services';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, BDRBdrDataRegulatorReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-bdr-verification-review-group-decision',
  templateUrl: './bdr-verification-review-group-decision.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule],
  providers: [bdrVerificationReviewGroupDecisionFormProvider],
})
export class BdrVerificationReviewGroupDecisionComponent implements PendingRequest {
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.bdrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as BDRBdrDataRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.bdrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  isEditable$ = this.bdrService.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(BDR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly bdrService: BdrService,
    readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.bdrService.postGroupDecisionReview(
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
