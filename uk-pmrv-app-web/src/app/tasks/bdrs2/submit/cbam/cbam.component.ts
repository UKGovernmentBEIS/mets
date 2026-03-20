import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { cbamFormProvider } from './cbam-form.provider';

@Component({
  selector: 'app-cbam',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './cbam.component.html',
  providers: [cbamFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CBAMComponent implements PendingRequest {
  isEditable$ = this.bdrs2Service.isEditable$;
  bdrS2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  returnLinkTitle = this.bdrs2Service.title();

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../upload-report';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.bdrS2Payload();
      this.bdrs2Service
        .postTaskSave(
          {
            bdrs2guardQuestions: {
              ...payload.bdrs2?.bdrs2guardQuestions,
              requiresAdditionalSubInstallationSplitsForCbam:
                this.form.value?.requiresAdditionalSubInstallationSplitsForCbam,
            },
            ...(this.form.value?.requiresAdditionalSubInstallationSplitsForCbam === false
              ? { mmpFiles: undefined }
              : {}),
          },
          {
            ...payload?.bdrs2Attachments,
          },
          false,
          'baseline',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
