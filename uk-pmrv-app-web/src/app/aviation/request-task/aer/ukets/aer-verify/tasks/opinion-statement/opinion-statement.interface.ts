import { FormControl, FormGroup } from '@angular/forms';

import { AviationAerOpinionStatement } from 'pmrv-api';

export interface AviationAerOpinionStatementFormModel {
  emissionsGroup: FormGroup<{
    fuelTypes: FormControl<AviationAerOpinionStatement['fuelTypes'] | null>;
    monitoringApproachType: FormControl<AviationAerOpinionStatement['monitoringApproachType'] | null>;
    emissionsCorrect: FormControl<AviationAerOpinionStatement['emissionsCorrect'] | null>;
  }>;

  changesGroup: FormGroup<{
    manuallyProvidedEmissions?: FormControl<AviationAerOpinionStatement['manuallyProvidedEmissions'] | null>;
    additionalChangesNotCovered: FormControl<AviationAerOpinionStatement['additionalChangesNotCovered'] | null>;
    additionalChangesNotCoveredDetails?: FormControl<
      AviationAerOpinionStatement['additionalChangesNotCoveredDetails'] | null
    >;
  }>;

  siteVisit: FormGroup<{
    type: FormControl<AviationAerOpinionStatement['siteVisit']['type'] | null>;
  }>;
}
