import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-bdr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, ReviewGroupDecisionSharedComponent],
  template: `
    <app-bdr-task-review returnToLink="../" [breadcrumb]="true">
      <app-page-heading caption="Overall decision">Review the overall decision</app-page-heading>
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm().overallDecision"></app-shared-overall-decision-summary-template>
      <app-shared-review-group-decision
        [isEditable]="isEditable()"
        [payload]="$any(bdrPayload())"
        [requestTaskId]="requestTaskId"
        [hideAmendsOption]="true"
        (formSubmit)="onSubmit($event)"></app-shared-review-group-decision>
    </app-bdr-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOverallDecisionReviewComponent {
  readonly isEditable: Signal<boolean> = this.bdrService.isEditable;
  readonly bdrPayload: Signal<BDRApplicationVerificationSubmitRequestTaskPayload> = this.bdrService.payload;
  readonly requestTaskId = this.bdrService.requestTaskId;
  readonly vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.bdrPayload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(form: UntypedFormGroup): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.bdrService.postGroupDecisionReview(
            constructReviewDecision(form),
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
  }
}
