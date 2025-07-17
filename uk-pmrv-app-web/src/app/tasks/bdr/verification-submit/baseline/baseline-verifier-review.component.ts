import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrReviewGroupDecisionComponent } from '@tasks/bdr/shared/components/decision/bdr-review-group-decision/bdr-review-group-decision.component';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import {
  BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-baseline-verifier-review',
  templateUrl: './baseline-verifier-review.component.html',
  standalone: true,
  imports: [
    SharedModule,
    TaskSharedModule,
    BdrTaskSharedModule,
    BaselineSummaryTemplateComponent,
    BdrReviewGroupDecisionComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineVerifierReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;

  bdr = computed(() => {
    const payload = this.payload() as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
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
  ) {}
}
