import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { alrReasonFormProvider } from './close-reason-form.provider';

@Component({
  selector: 'app-alr-close-reason',
  imports: [SharedModule, AlrTaskSharedModule],
  template: `
    <app-alr-task-common [breadcrumb]="true" returnLink="../../../">
      <app-wizard-step
        (formSubmit)="onSubmit()"
        [formGroup]="form"
        caption="Close task"
        heading="Provide a reason to support your decision"
        submitText="Continue"
        [hideSubmit]="!isEditable()">
        <div govuk-textarea formControlName="reason" [maxLength]="10000"></div>
      </app-wizard-step>
    </app-alr-task-common>
  `,
  providers: [alrReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReasonComponent {
  isEditable = this.alrService.isEditable;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    const nextWizardStep = ['../', 'latest-activity'];

    if (!this.form.dirty) {
      this.router.navigate(nextWizardStep, {
        relativeTo: this.route,
      });
    } else {
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload?.regulatorReviewOutcome,
                determination: {
                  ...payload.regulatorReviewOutcome.determination,
                  ...this.form.value,
                },
              },
              'DETERMINATION',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(nextWizardStep, {
            relativeTo: this.route,
          }),
        );
    }
  }
}
