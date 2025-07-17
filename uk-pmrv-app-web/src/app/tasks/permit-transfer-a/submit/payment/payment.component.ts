import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { paymentFormProvider } from './payment-form.provider';

@Component({
  selector: 'app-payment',
  template: `
    <app-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      heading="Who will be paying for the transfer?"
      [hideSubmit]="hideSubmit$ | async">
      <div formControlName="payer" govuk-radio legendSize="medium">
        <govuk-radio-option
          value="TRANSFERER"
          label="The business or organisation making the transfer"></govuk-radio-option>
        <govuk-radio-option
          value="RECEIVER"
          label="The business or organisation receiving the transfer"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="..">Return to: Permit transfer application</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [paymentFormProvider],
})
export class TransferAPaymentComponent {
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
      this.router.navigate(['..', 'aem-report'], { relativeTo: this.route });
    } else {
      this.permitTransferAService
        .sendDataForPost({
          payer: this.form.value.payer,
        } as Partial<PermitTransferAApplicationRequestTaskPayload>)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'aem-report'], { relativeTo: this.route }));
    }
  }
}
