import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

import { alrAuthorityDateSubmittedFormProvider } from './date-submitted-form.provider';

@Component({
  selector: 'app-alr-date-submitted',
  imports: [AlrTaskSharedModule, SharedModule],
  template: `
    <app-alr-task-common [breadcrumb]="true" returnLink="../">
      <app-wizard-step
        [formGroup]="form"
        (formSubmit)="onSubmit()"
        heading="Provide the date application was submitted to the authority"
        caption="Provide the date application was submitted to UK authorities"
        submitText="Continue"
        [hideSubmit]="!isEditable()">
        <div
          formControlName="submissionDate"
          hint="This date cannot be in the future"
          govuk-date-input
          [isRequired]="true"
          [max]="today"></div>
      </app-wizard-step>
    </app-alr-task-common>
  `,
  providers: [alrAuthorityDateSubmittedFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthorityDateSubmittedComponent {
  private readonly payload = this.alrService.payload as Signal<ALRAuthorityResponseSubmitRequestTaskPayload>;

  isEditable = this.alrService.isEditable;
  today = new Date();

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    const nextStep = ['../', 'summary'];

    if (!this.form.dirty) {
      this.router.navigate(nextStep, { relativeTo: this.route });
    } else {
      const payload = this.payload();
      const formValues = this.form.value;

      this.alrService
        .postAlrAuthority(
          { ...payload?.authorityReviewOutcome, submissionDate: formValues.submissionDate },
          'applicationSubmitted',
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextStep, { relativeTo: this.route }));
    }
  }
}
