import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { codeFormProvider } from './code-form.provider';

@Component({
  selector: 'app-transfer-code',
  template: `
    <app-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      heading="Transfer code from permit recipient"
      [hideSubmit]="hideSubmit$ | async"
    >
      <p class="govuk-body">
        You can request this from the business or organisation receiving the permit transfer.
        <br /><br />
        If the operator does not have a code, they must apply for a new account first. Once their application has been
        approved they will get a code to share.
      </p>
      <h4 class="govuk-heading-s">Enter the nine digit transfer code</h4>
      <div class="govuk-hint">For example 135758421</div>
      <div formControlName="transferCode" govuk-text-input></div>
    </app-wizard-step>
    <a govukLink routerLink="..">Return to: Permit transfer application</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [codeFormProvider],
})
export class TransferACodeComponent {
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
      this.router.navigate(['..', 'summary'], { relativeTo: this.route });
    } else {
      this.permitTransferAService
        .sendDataForPost({
          transferCode: this.form.value.transferCode,
        } as Partial<PermitTransferAApplicationRequestTaskPayload>)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'summary'], { relativeTo: this.route }));
    }
  }
}
