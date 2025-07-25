import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { scheduleMeasurementsFormProvider } from './schedule-measurements-form.provider';

@Component({
  selector: 'app-schedule-measurements',
  template: `
    <app-permit-task [breadcrumb]="[{ text: 'CALCULATION_PFC' | monitoringApproachDescription, link: ['pfc'] }]">
      <app-wizard-step
        (formSubmit)="onContinue()"
        [formGroup]="form"
        [showBackLink]="true"
        submitText="Continue"
        [caption]="'CALCULATION_PFC' | monitoringApproachDescription"
        heading="Provide details about the procedures for setting out the measurement schedule"
        [hideSubmit]="(store.isEditable$ | async) === false">
        <app-procedure-form></app-procedure-form>
      </app-wizard-step>
      <app-approach-return-link
        [parentTitle]="'CALCULATION_PFC' | monitoringApproachDescription"
        reviewGroupUrl="pfc"
        [isNested]="true"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [scheduleMeasurementsFormProvider],
})
export class ScheduleMeasurementsComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      this.store
        .patchTask(
          this.route.snapshot.data.taskKey,
          {
            exist: true,
            scheduleMeasurements: this.form.value,
          },
          false,
          this.route.snapshot.data.statusKey,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}
