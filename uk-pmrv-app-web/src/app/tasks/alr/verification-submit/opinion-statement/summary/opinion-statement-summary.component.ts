import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationVerificationSubmitRequestTaskPayload, ALRVerificationOpinionStatement } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  opinionStatementFiles: AttachedFile[];
  supportingFiles: AttachedFile[];
  notes: ALRVerificationOpinionStatement['notes'];
  hideSubmit: boolean;
}

@Component({
  selector: 'app-alr-opinion-statement-summary',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule],
  templateUrl: './opinion-statement-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOpinionStatementSummaryComponent {
  isEditable = this.alrService.isEditable;
  payload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const {
      verificationSectionsCompleted,
      verificationReport: { opinionStatement: { opinionStatementFiles, supportingFiles, notes } = {} } = {},
    } = this.payload();

    return {
      isEditable,
      opinionStatementFiles: this.alrService.getVerifierDownloadUrlFiles(opinionStatementFiles),
      supportingFiles: this.alrService.getVerifierDownloadUrlFiles(supportingFiles),
      notes,
      hideSubmit: !verificationSectionsCompleted?.['opinionStatement']?.[0] && isEditable,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.alrService
      .postVerificationTaskSave(null, true, 'opinionStatement')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
