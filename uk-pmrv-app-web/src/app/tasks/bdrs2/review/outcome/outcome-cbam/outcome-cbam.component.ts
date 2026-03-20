import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDRS2_TASK_FORM, BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { bdrs2CbamProvider } from './outcome-cbam-form.provider';

@Component({
  selector: 'app-bdrs2-outcome-cbam',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  templateUrl: './outcome-cbam.component.html',
  providers: [bdrs2CbamProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2OutcomeCbamComponent implements PendingRequest {
  isEditable = this.bdrs2Service.isEditable;
  bdrPayload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '../file-upload';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.bdrs2Service
        .postRegulatorTaskSave(
          {
            cbamSplitOpinion: this.form.value.cbamSplitOpinion,
            cbamSplitReviewNotes: {
              operatorNotes: this.form.value.operatorNotes,
              internalNotes: this.form.value.internalNotes,
            },
          },
          false,
          'outcome',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
