import { FormControl, FormGroup } from '@angular/forms';

import { AviationAerUncorrectedNonConformities, UncorrectedItem, VerifierComment } from 'pmrv-api';

export interface AviationAerUncorrectedNonConformitiesFormModel {
  existUncorrectedNonConformities: FormControl<
    AviationAerUncorrectedNonConformities['existUncorrectedNonConformities'] | null
  >;

  uncorrectedNonConformitiesGroup?: FormGroup<{
    uncorrectedNonConformities: FormControl<UncorrectedItemFormModel[] | null>;
  }>;

  existPriorYearIssues?: FormControl<AviationAerUncorrectedNonConformities['existPriorYearIssues'] | null>;

  priorYearIssuesGroup: FormGroup<{
    priorYearIssues: FormControl<VerifierCommentFormModel[] | null>;
  }>;
}

export interface UncorrectedItemFormModel {
  reference: FormControl<UncorrectedItem['reference'] | null>;
  explanation: FormControl<UncorrectedItem['explanation'] | null>;
  materialEffect: FormControl<UncorrectedItem['materialEffect'] | null>;
}

export interface VerifierCommentFormModel {
  reference: FormControl<VerifierComment['reference'] | null>;
  explanation: FormControl<VerifierComment['explanation'] | null>;
}
