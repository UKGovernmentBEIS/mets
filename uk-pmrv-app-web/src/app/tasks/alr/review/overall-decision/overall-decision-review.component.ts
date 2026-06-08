import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-alr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, ReviewGroupDecisionSharedComponent],
  template: `
    <app-alr-task-common returnLink="../" [breadcrumb]="true">
      <app-page-heading caption="Overall decision">Review the overall decision</app-page-heading>
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm().overallDecision"></app-shared-overall-decision-summary-template>
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
export class AlrOverallDecisionReviewComponent {
  get downloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  readonly isEditable: Signal<boolean> = this.alrService.isEditable;
  readonly alrPayload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;
  readonly requestTaskId = this.alrService.requestTaskId;

  readonly vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.alrPayload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
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
