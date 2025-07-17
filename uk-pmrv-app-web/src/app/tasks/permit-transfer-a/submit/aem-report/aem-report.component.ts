import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { aemReportFormProvider } from './aem-report-form.provider';

@Component({
  selector: 'app-aem-report',
  template: `
    <app-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      heading="Who will complete the annual emission report?"
      [hideSubmit]="hideSubmit$ | async">
      <div formControlName="aerLiable" govuk-radio legendSize="medium">
        <govuk-radio-option
          value="TRANSFERER"
          label="We the transferring operator will complete it"></govuk-radio-option>
        <govuk-radio-option value="RECEIVER" label="The new operator will complete it"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="..">Return to: Permit transfer application</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [aemReportFormProvider],
})
export class TransferAAemReportComponent {
  hideSubmit$ = this.permitTransferAService.isEditable$.pipe(map((isEditable) => !isEditable));

  constructor(
    @Inject(PERMIT_TRANSFER_A_FORM) readonly form: FormGroup,
    readonly permitTransferAService: PermitTransferAService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (!this.form.dirty) {
      this.router.navigate(['..', 'code'], { relativeTo: this.route });
    } else {
      this.permitTransferAService
        .sendDataForPost({
          aerLiable: this.form.value.aerLiable,
        } as Partial<PermitTransferAApplicationRequestTaskPayload>)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'code'], { relativeTo: this.route }));
    }
  }
}
