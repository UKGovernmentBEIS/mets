import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import {
  OutcomeSummaryTemplateComponent,
  ViewModel,
} from '@shared/components/bdrs2/outcome-summary-template/outcome-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-outcome-summary',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, OutcomeSummaryTemplateComponent],
  templateUrl: './outcome-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeSummaryComponent {
  readonly isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  bdrs2Payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;

  readonly vm: Signal<ViewModel> = computed(() => {
    const outcome = this.bdrs2Payload().regulatorReviewOutcome;
    const bdrFile = outcome?.file ? this.bdrs2Service.getRegulatorDownloadUrlFiles([outcome?.file])[0] : null;
    const files = outcome?.supportingFiles
      ? this.bdrs2Service.getRegulatorDownloadUrlFiles(outcome?.supportingFiles)
      : [];

    return {
      outcome,
      bdrs2: this.bdrs2Payload().bdrs2,
      bdrFile,
      files,
      isEditable: this.isEditable(),
    };
  });

  readonly hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.bdrs2Payload().regulatorReviewSectionsCompleted?.['outcome'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.bdrs2Payload();
    this.bdrs2Service
      .postRegulatorTaskSave(
        {
          ...payload?.regulatorReviewOutcome,
        },
        true,
        'outcome',
        {
          ...payload?.bdrs2Attachments,
        },
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
