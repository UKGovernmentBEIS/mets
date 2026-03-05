import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import {
  OutcomeSummaryTemplateComponent,
  ViewModel,
} from '../../../../../shared/components/bdr/outcome-summary-template/outcome-summary-template.component';

@Component({
  selector: 'app-outcome-summary',
  templateUrl: './outcome-summary.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, OutcomeSummaryTemplateComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeSummaryComponent {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const outcome = this.bdrPayload().regulatorReviewOutcome;

    return {
      outcome,
      bdr: this.bdrPayload().bdr,
      bdrFile: outcome?.bdrFile ? this.bdrService.getRegulatorDownloadUrlFiles([outcome?.bdrFile])[0] : null,
      files: outcome?.files ? this.bdrService.getRegulatorDownloadUrlFiles(outcome?.files) : [],
      isEditable: this.isEditable(),
    };
  });

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.bdrPayload().regulatorReviewSectionsCompleted?.['outcome'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.bdrPayload();
    this.bdrService
      .postRegulatorTaskSave(
        {
          ...payload?.regulatorReviewOutcome,
        },
        true,
        'outcome',
        {
          ...payload?.bdrAttachments,
        },
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
