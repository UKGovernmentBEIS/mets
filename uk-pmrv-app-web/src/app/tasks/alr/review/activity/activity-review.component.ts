import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-activity-review',
  imports: [
    SharedModule,
    TaskSharedModule,
    AlrTaskSharedModule,
    ActivitySummaryTemplateComponent,
    ReviewGroupDecisionSharedComponent,
  ],
  template: `
    <app-alr-task-common
      [breadcrumb]="true"
      heading="Review the activity level report and details"
      caption="Activity level report details">
      <app-alr-activity-summary-template
        [isEditable]="false"
        [alrFile]="alrFile()"
        [files]="files()"></app-alr-activity-summary-template>

      <app-shared-review-group-decision
        [isEditable]="isEditable()"
        [payload]="$any(payload())"
        [downloadUrl]="downloadUrl"
        [requestTaskId]="requestTaskId"
        (formSubmit)="onSubmit($event)"></app-shared-review-group-decision>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityReviewComponent {
  get downloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  readonly payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  readonly isEditable = this.alrService.isEditable;
  readonly requestTaskId = this.alrService.requestTaskId;

  alr = computed(() => {
    const payload = this.payload();
    return payload.alr;
  });

  alrFile = computed(() => {
    const alr = this.alr();
    return alr?.alrFile ? this.alrService.getOperatorDownloadUrlAlrFile(alr.alrFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const alr = this.alr();
    return alr?.files ? this.alrService.getOperatorDownloadUrlFiles(alr.files) : [];
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(form: UntypedFormGroup) {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.alrService.postGroupReviewDecision(
            constructReviewDecision(form),
            'ALR_DATA',
            data.groupKey,
            'ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
            form.controls.requiredChanges.value.map((requiredChange: any) => requiredChange.files).flat(),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
