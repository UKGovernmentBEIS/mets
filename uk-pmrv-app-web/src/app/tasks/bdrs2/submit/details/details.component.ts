import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { detailsFormProvider } from './details-form.provider';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  providers: [detailsFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements PendingRequest {
  isEditable$ = this.bdrs2Service.isEditable$;
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  returnLinkTitle = this.bdrs2Service.title();

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const payload = this.bdrs2Payload();
    const isYesFAquestion = payload.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType !== 'WITHDRAW';
    const nextRoute = isYesFAquestion && this.form.value?.inEiteSector === true ? '../cbam' : '../upload-report';
    const isEiteSectorFalse = this.form.value?.inEiteSector === false;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.bdrs2Service
        .postTaskSave(
          {
            bdrs2guardQuestions: {
              ...payload.bdrs2?.bdrs2guardQuestions,
              covidAdjustments: this.form.value?.covidAdjustments,
              inEiteSector: this.form.value?.inEiteSector,
              ...(isEiteSectorFalse ? { requiresAdditionalSubInstallationSplitsForCbam: undefined } : {}),
            },
            ...(isEiteSectorFalse ? { mmpFiles: undefined } : {}),
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
