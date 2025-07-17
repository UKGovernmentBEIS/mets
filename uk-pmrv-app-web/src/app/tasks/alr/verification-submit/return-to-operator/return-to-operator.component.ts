import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

@Component({
  selector: 'app-alr-verifier-return-to-operator',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule],
  template: `
    <app-alr-task-review
      [breadcrumb]="true"
      heading="Changes required by the operator"
      caption="Return to operator for changes">
      <app-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" [hideSubmit]="!isEditable()">
        <p class="govuk-body">
          The operator will be notified by email that the report has been returned for changes.
          <br />
          Verification progress is saved so that you can continue your review if the operator resubmits the report for
          verification.
          <br />
          This will be sent to the operator when you return the report
        </p>

        <div formControlName="changesRequired" [maxLength]="10000" govuk-textarea></div>
      </app-wizard-step>
    </app-alr-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReturnToOperatorComponent {
  isEditable = this.alrService.isEditable;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
