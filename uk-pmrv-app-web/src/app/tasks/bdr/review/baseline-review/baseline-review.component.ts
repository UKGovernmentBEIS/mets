import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-baseline-review',
  imports: [
    SharedModule,
    TaskSharedModule,
    BdrTaskSharedModule,
    BaselineSummaryTemplateComponent,
    ReviewGroupDecisionSharedComponent,
  ],
  templateUrl: './baseline-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineReviewComponent {
  get downloadUrl() {
    return this.bdrService.getBaseFileDownloadUrl();
  }

  readonly notification = this.router.currentNavigation()?.extras.state?.notification;
  readonly isEditable = this.bdrService.isEditable;
  readonly requestTaskId = this.bdrService.requestTaskId;
  readonly payload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  bdr = computed(() => {
    const payload = this.payload();
    return payload.bdr;
  });

  bdrFile = computed(() => {
    const bdr = this.bdr();
    return bdr?.bdrFile ? this.bdrService.getOperatorDownloadUrlBdrFile(bdr.bdrFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const bdr = this.bdr();
    return bdr?.files ? this.bdrService.getOperatorDownloadUrlFiles(bdr.files) : [];
  });
  mmpFiles: Signal<AttachedFile[]> = computed(() => {
    const bdr = this.bdr();
    return bdr?.mmpFiles ? this.bdrService.getOperatorDownloadUrlFiles(bdr.mmpFiles) : [];
  });

  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(form: UntypedFormGroup): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.bdrService.postGroupDecisionReview(
            constructReviewDecision(form),
            'BDR_DATA',
            data.groupKey,
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
