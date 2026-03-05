import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { alrPreliminaryAllocationFormProvider } from './preliminary-allocation-form.provider';

@Component({
  selector: 'app-alr-preliminary-allocation',
  standalone: true,
  imports: [SharedModule, AlrTaskSharedModule],
  template: `
    <app-alr-task-common [breadcrumb]="true" returnLink="../../../">
      <app-wizard-step
        [formGroup]="form"
        (formSubmit)="onSubmit()"
        heading="Will you send a preliminary allocation letter?"
        caption="Proceed to UK ETS authority"
        submitText="Continue"
        [hideSubmit]="!isEditable()">
        <div formControlName="needsOfficialNotice" govuk-radio>
          <govuk-radio-option label="Yes" [value]="true"></govuk-radio-option>
          <govuk-radio-option label="No" [value]="false"></govuk-radio-option>
        </div>
      </app-wizard-step>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [alrPreliminaryAllocationFormProvider],
})
export class AlrPreliminaryAllocationComponent {
  isEditable = this.alrService.isEditable;

  private readonly payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    const nextStep = ['../summary'];

    if (!this.form.dirty) {
      this.router.navigate(nextStep, { relativeTo: this.route });
    } else {
      const payload = this.payload();

      this.alrService
        .postAlrReview(
          {
            ...payload?.regulatorReviewOutcome,
            determination: {
              ...payload.regulatorReviewOutcome.determination,
              ...this.form.value,
            },
          },
          'DETERMINATION',
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextStep, { relativeTo: this.route }));
    }
  }
}
