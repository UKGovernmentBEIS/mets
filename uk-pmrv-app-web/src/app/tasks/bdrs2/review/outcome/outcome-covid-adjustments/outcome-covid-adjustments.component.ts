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

import { bdrs2CovidAdjustmentsFormProvider } from './outcome-covid-adjustments-form.provider';

@Component({
  selector: 'app-bdrs2-outcome-covid-adjustments',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  templateUrl: './outcome-covid-adjustments.component.html',
  providers: [bdrs2CovidAdjustmentsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2OutcomeCovidAdjustmentsComponent implements PendingRequest {
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
    if (!this.form.dirty) {
      this.router.navigate(['../installation-sector'], { relativeTo: this.route });
    } else {
      this.bdrs2Service
        .postRegulatorTaskSave(
          {
            covidAdjustmentsOpinion: this.form.value.covidAdjustmentsOpinion,
            covidAdjustmentsReviewNotes: {
              operatorNotes: this.form.value.operatorNotes,
              internalNotes: this.form.value.internalNotes,
            },
          },
          false,
          'outcome',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../installation-sector'], { relativeTo: this.route }));
    }
  }
}
