import { FormControl, FormGroup } from '@angular/forms';

import { AviationAerMaterialityLevel } from 'pmrv-api';

export interface AviationAerMaterialityLevelFormModel {
  materialityDetails: FormControl<AviationAerMaterialityLevel['materialityDetails'] | null>;

  accreditationReferenceDocumentTypesGroup: FormGroup<{
    accreditationReferenceDocumentTypes: FormControl<
      AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'] | null
    >;
  }>;

  otherReference: FormControl<AviationAerMaterialityLevel['otherReference'] | null>;
}
