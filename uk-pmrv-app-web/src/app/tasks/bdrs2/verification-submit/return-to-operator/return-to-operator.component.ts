import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';

@Component({
  selector: 'app-bdrs2-verifier-return-to-operator',
  imports: [BdrS2TaskSharedModule, SharedModule],
  standalone: true,
  template: `
    <app-bdrs2-task-review
      [breadcrumb]="true"
      heading="Changes required by the operator"
      caption="Return to operator for changes">
      <app-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" [hideSubmit]="!isEditable()">
        <p class="govuk-body govuk-!-width-three-quarters">
          The operator will be notified by email that the report has been returned for changes.
          <br />
          Verification progress is saved so that you can continue your review if the operator resubmits the report for
          verification.
          <br />
          This will be sent to the operator when you return the report
        </p>
        <div
          formControlName="changesRequired"
          [maxLength]="10000"
          govuk-textarea
          class="govuk-!-width-three-quarters"></div>
      </app-wizard-step>
    </app-bdrs2-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2ReturnToOperatorComponent {
  isEditable = this.bdrs2Service.isEditable;

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
