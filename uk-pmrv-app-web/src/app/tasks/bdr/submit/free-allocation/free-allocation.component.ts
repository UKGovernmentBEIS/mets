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

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { freeAllocationFormProvider } from './free-allocation-form.provider';

@Component({
  selector: 'app-free-allocation',
  templateUrl: './free-allocation.component.html',
  providers: [freeAllocationFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FreeAllocationComponent implements PendingRequest {
  isEditable$ = this.bdrService.isEditable$;
  bdrPayload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = this.form.value?.isApplicationForFreeAllocation ? '../provide-mmp' : '../summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.bdrPayload();
      this.bdrService
        .postTaskSave(
          {
            isApplicationForFreeAllocation: this.form.value?.isApplicationForFreeAllocation,
            statusApplicationType: this.form.value?.statusApplicationType,
            infoIsCorrectChecked: this.form.value?.infoIsCorrectChecked?.[0],
            hasMmp: this.form.value?.isApplicationForFreeAllocation ? null : false,
          },
          {
            ...payload?.bdrAttachments,
          },
          false,
          'baseline',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
