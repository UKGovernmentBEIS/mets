import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

import { createBatchClaimsForm } from './util/batch-claims-form.helper';
import { createComplianceWithUkEtsRequirementsForm } from './util/compliance-ukets-requirements-form.helper';
import { createDoubleCountingConfirmationForm } from './util/double-counting-confirmation-form.helper';
import { createEvidenceCriteriaForm } from './util/evidence-criteria-form.helper';
import { createReviewResultsForm } from './util/review-results-form.helper';
import { AviationAerEmissionsReductionClaimVerificationFormModel } from './verify-emissions-reduction-claim.interface';

@Injectable()
export class VerifyEmissionsReductionClaimFormProvider
  implements
    TaskFormProvider<
      AviationAerEmissionsReductionClaimVerification,
      AviationAerEmissionsReductionClaimVerificationFormModel
    >
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(
    aviationAerEmissionsReductionClaimVerification: AviationAerEmissionsReductionClaimVerification | undefined,
  ) {
    if (aviationAerEmissionsReductionClaimVerification) {
      const value: AviationAerEmissionsReductionClaimVerification = {
        safBatchClaimsReviewed: aviationAerEmissionsReductionClaimVerification.safBatchClaimsReviewed ?? null,
        batchReferencesNotReviewed: aviationAerEmissionsReductionClaimVerification.batchReferencesNotReviewed ?? null,
        dataSampling: aviationAerEmissionsReductionClaimVerification.dataSampling ?? null,
        reviewResults: aviationAerEmissionsReductionClaimVerification.reviewResults ?? null,
        noDoubleCountingConfirmation:
          aviationAerEmissionsReductionClaimVerification.noDoubleCountingConfirmation ?? null,
        evidenceAllCriteriaMetExist: aviationAerEmissionsReductionClaimVerification.evidenceAllCriteriaMetExist ?? null,
        noCriteriaMetIssues: aviationAerEmissionsReductionClaimVerification.noCriteriaMetIssues ?? null,
        complianceWithUkEtsRequirementsExist:
          aviationAerEmissionsReductionClaimVerification.complianceWithUkEtsRequirementsExist ?? null,
        notCompliedWithUkEtsRequirementsAspects:
          aviationAerEmissionsReductionClaimVerification.notCompliedWithUkEtsRequirementsAspects ?? null,
      };

      this.form.setValue(value);
    }
  }

  getFormValue(): AviationAerEmissionsReductionClaimVerification {
    return this.form.value as AviationAerEmissionsReductionClaimVerification;
  }

  private _buildForm() {
    this._form = this.fb.group(
      {
        ...createBatchClaimsForm(this.destroy$),
        ...createReviewResultsForm(),
        ...createDoubleCountingConfirmationForm(),
        ...createEvidenceCriteriaForm(this.destroy$),
        ...createComplianceWithUkEtsRequirementsForm(this.destroy$),
      },
      { updateOn: 'change' },
    );
  }
}
