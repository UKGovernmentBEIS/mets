import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, ReviewGroupDecisionSharedComponent],
  template: `
    <app-alr-task-common returnLink="../" [breadcrumb]="true">
      <app-page-heading caption="Activity level report verification opinion statement">
        Review the activity level report verification opinion statement
      </app-page-heading>

      <app-opinion-statement-summary-template
        [notes]="alrPayload()?.verificationReport?.opinionStatement?.notes"
        [opinionStatementFile]="opinionStatementFile()"
        opinionStatementFilesText="Uploaded ALR verification opinion statement"></app-opinion-statement-summary-template>

      <app-shared-review-group-decision
        [isEditable]="isEditable()"
        [payload]="$any(alrPayload())"
        [downloadUrl]="downloadUrl"
        [requestTaskId]="requestTaskId"
        [hideAmendsOption]="true"
        (formSubmit)="onSubmit($event)"></app-shared-review-group-decision>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOpinionStatementReviewComponent {
  get downloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  readonly isEditable: Signal<boolean> = this.alrService.isEditable;
  readonly alrPayload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;
  readonly requestTaskId = this.alrService.requestTaskId;

  readonly opinionStatementFile: Signal<AttachedFile> = computed(() => {
    const payload = this.alrPayload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFile
      ? this.alrService.getVerifierDownloadUrlFile(payload?.verificationReport?.opinionStatement?.opinionStatementFile)
      : ({} as AttachedFile);
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getDownloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  onSubmit(form: UntypedFormGroup) {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.alrService.postGroupReviewDecision(
            constructReviewDecision(form),
            'VERIFICATION_REPORT_DATA',
            data.groupKey,
            'ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
            [],
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
