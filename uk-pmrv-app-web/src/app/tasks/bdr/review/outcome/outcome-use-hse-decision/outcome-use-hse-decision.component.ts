import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { freeAllocationFormProvider } from './outcome-use-hse-decision-form.provider';

@Component({
  selector: 'app-outcome-use-hse-decision',
  templateUrl: './outcome-use-hse-decision.component.html',
  providers: [freeAllocationFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeUseHseDecisionComponent implements PendingRequest {
  isEditable = this.bdrService.isEditable;
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  isHSEApplied = this.bdrPayload().bdr?.statusApplicationType === 'HSE';
  isUSEApplied = this.bdrPayload().bdr?.statusApplicationType === 'USE';

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const goingToquestion27a: boolean =
      this.bdrPayload().regulatorReviewOutcome?.hasRegulatorSentFreeAllocation === false &&
      this.form.value?.hasRegulatorSentHSE === false &&
      this.form.value?.hasRegulatorSentUSE === false;

    const nextRoute = goingToquestion27a ? '../27a-question' : '../upload-files';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.bdrService
        .postRegulatorTaskSave(
          {
            ...this.form.value,
            ...(!goingToquestion27a
              ? { hasOperatorMetDataSubmissionRequirements: undefined }
              : {
                  hasOperatorMetDataSubmissionRequirements:
                    this.bdrPayload().regulatorReviewOutcome?.hasOperatorMetDataSubmissionRequirements,
                }),
          },
          false,
          'outcome',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
