import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { dateFormProvider } from './date-form.provider';

@Component({
  selector: 'app-date',
  template: `
    <app-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      heading="What date will the transfer take place?"
      [hideSubmit]="hideSubmit$ | async"
    >
      <p class="govuk-body">
        This means the agreed change of ownership between you and the operator you are transferring to.
      </p>
      <div class="govuk-hint">For example 27 3 2007</div>
      <div formControlName="transferDate" govuk-date-input [isRequired]="true"></div>
    </app-wizard-step>
    <a govukLink routerLink="..">Return to: Permit transfer application</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [dateFormProvider],
})
export class TransferADateComponent {
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
      this.router.navigate(['..', 'payment'], { relativeTo: this.route });
    } else {
      this.permitTransferAService
        .sendDataForPost({
          transferDate: this.form.value.transferDate,
        } as Partial<PermitTransferAApplicationRequestTaskPayload>)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'payment'], { relativeTo: this.route }));
    }
  }
}
