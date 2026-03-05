import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { verifierDetailsFormProvider } from '@tasks/aer/verification-submit/verifier-details/verifier-details-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-verifier-details',
  templateUrl: './verifier-details.component.html',
  providers: [verifierDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  verificationReportData$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport));
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postVerificationTaskSave(
          {
            verifierContact: {
              name: this.form.get('name').value,
              email: this.form.get('email').value,
              phoneNumber: this.form.get('phoneNumber').value,
            },
            verificationTeamDetails: {
              leadEtsAuditor: this.form.get('leadEtsAuditor').value,
              etsAuditors: this.form.get('etsAuditors').value,
              etsTechnicalExperts: this.form.get('etsTechnicalExperts').value,
              independentReviewer: this.form.get('independentReviewer').value,
              technicalExperts: this.form.get('technicalExperts').value,
              authorisedSignatoryName: this.form.get('authorisedSignatoryName').value,
            },
          },
          false,
          'verificationTeamDetails',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }
}
