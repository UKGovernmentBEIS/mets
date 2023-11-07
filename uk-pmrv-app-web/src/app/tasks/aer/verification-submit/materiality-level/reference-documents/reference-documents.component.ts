import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { referenceDocumentsFormProvider } from '@tasks/aer/verification-submit/materiality-level/reference-documents/reference-documents-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import {
  euEtsVerificationConductAccreditedVerifiers,
  euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders,
  euEtsVerificationRules,
  ukEtsVerificationConductAccreditedVerifiers,
  ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders,
  ukEtsVerificationRules,
} from './reference-document-types';

@Component({
  selector: 'app-reference-documents',
  templateUrl: './reference-documents.component.html',
  providers: [referenceDocumentsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReferenceDocumentsComponent {
  isEditable$ = this.aerService.isEditable$;

  ukEtsVerificationConductAccreditedVerifiers = ukEtsVerificationConductAccreditedVerifiers;
  ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders =
    ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders;
  ukEtsVerificationRules = ukEtsVerificationRules;
  euEtsVerificationConductAccreditedVerifiers = euEtsVerificationConductAccreditedVerifiers;
  euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders =
    euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders;
  euEtsVerificationRules = euEtsVerificationRules;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../summary'], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.materialityLevel,
          ),
          switchMap((materialityLevelInfo) =>
            this.aerService.postVerificationTaskSave(
              {
                materialityLevel: {
                  ...materialityLevelInfo,
                  accreditationReferenceDocumentTypes: [
                    ...(this.form.get('ukEtsVerificationConductAccreditedVerifiers').value || []),
                    ...(this.form.get('ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders').value || []),
                    ...(this.form.get('ukEtsVerificationRules').value || []),
                    ...(this.form.get('euEtsVerificationConductAccreditedVerifiers').value || []),
                    ...(this.form.get('euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders').value || []),
                    ...(this.form.get('euEtsVerificationRules').value || []),
                    ...(this.form.get('otherReferences').value || []),
                  ],
                  otherReference: this.form.get('otherReferences').value?.length
                    ? this.form.get('otherReference').value
                    : null,
                },
              },
              false,
              'materialityLevel',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route }));
    }
  }
}
