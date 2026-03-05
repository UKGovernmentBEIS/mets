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

import { freeAllocationFormProvider } from './outcome-fa-decision-form.provider';

@Component({
  selector: 'app-outcome-fa-decision',
  templateUrl: './outcome-fa-decision.component.html',
  providers: [freeAllocationFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeFaDecisionComponent implements PendingRequest {
  isEditable = this.bdrService.isEditable;
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  isFAApplied = this.bdrPayload().bdr?.isApplicationForFreeAllocation;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '../use-hse-decision';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.bdrService
        .postRegulatorTaskSave(
          {
            ...this.form.value,
          },
          false,
          'outcome',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
