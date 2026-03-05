import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';

@Component({
  selector: 'app-bdr-verifier-return-to-operator',
  standalone: true,
  imports: [BdrTaskSharedModule, SharedModule],
  template: `
    <app-bdr-task-review
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
    </app-bdr-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrReturnToOperatorComponent {
  isEditable = this.bdrService.isEditable;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
