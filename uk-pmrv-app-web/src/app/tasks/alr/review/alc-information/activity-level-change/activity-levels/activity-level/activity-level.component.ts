import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { alrActivityLevelFormProvider } from './activity-level-form.provider';

@Component({
  selector: 'app-alr-activity-level',
  imports: [AlrTaskSharedModule, SharedModule],
  template: `
    <app-alr-task-common
      [breadcrumb]="true"
      heading="Add new activity level change"
      caption="New activity level changes"
      returnLinkTitle="Information about this activity level change">
      <app-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" submitText="Continue" [hideSubmit]="!isEditable()">
        <app-activity-level-template-form [year]="year"></app-activity-level-template-form>
      </app-wizard-step>
    </app-alr-task-common>
  `,
  providers: [alrActivityLevelFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActivityLevelComponent {
  private readonly index = this.route.snapshot.paramMap.get('index');
  private readonly createMode = this.index === null;

  isEditable = this.alrService.isEditable;
  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  readonly year = this.alrService.year;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    const nextRoute = '../';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      const payload = this.payload();

      this.alrService
        .postAlrReview(
          {
            activityLevels: this.createMode
              ? [
                  ...(payload.regulatorReviewOutcome.activityLevels ?? []),
                  {
                    ...this.form.value,
                    activityLevelChangeId: (payload.regulatorReviewOutcome.activityLevels ?? []).length,
                  },
                ]
              : payload.regulatorReviewOutcome.activityLevels?.map((activityLevel, idx) =>
                  idx === Number(this.index) ? { ...this.form.value, activityLevelChangeId: idx } : activityLevel,
                ),
          },
          'ALC',
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
