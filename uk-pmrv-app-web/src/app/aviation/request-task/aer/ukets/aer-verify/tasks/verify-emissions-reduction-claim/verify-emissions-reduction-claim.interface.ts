import { FormControl } from '@angular/forms';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

export interface AviationAerEmissionsReductionClaimVerificationFormModel {
  safBatchClaimsReviewed: FormControl<AviationAerEmissionsReductionClaimVerification['safBatchClaimsReviewed'] | null>;

  batchReferencesNotReviewed?: FormControl<
    AviationAerEmissionsReductionClaimVerification['batchReferencesNotReviewed'] | null
  >;

  dataSampling?: FormControl<AviationAerEmissionsReductionClaimVerification['dataSampling'] | null>;
  reviewResults: FormControl<AviationAerEmissionsReductionClaimVerification['reviewResults'] | null>;

  noDoubleCountingConfirmation: FormControl<
    AviationAerEmissionsReductionClaimVerification['noDoubleCountingConfirmation'] | null
  >;

  evidenceAllCriteriaMetExist: FormControl<
    AviationAerEmissionsReductionClaimVerification['evidenceAllCriteriaMetExist'] | null
  >;

  noCriteriaMetIssues?: FormControl<AviationAerEmissionsReductionClaimVerification['noCriteriaMetIssues'] | null>;

  complianceWithUkEtsRequirementsExist: FormControl<
    AviationAerEmissionsReductionClaimVerification['complianceWithUkEtsRequirementsExist'] | null
  >;

  notCompliedWithUkEtsRequirementsAspects?: FormControl<
    AviationAerEmissionsReductionClaimVerification['notCompliedWithUkEtsRequirementsAspects'] | null
  >;
}
