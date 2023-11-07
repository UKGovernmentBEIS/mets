import { FormGroup } from '@angular/forms';

import { CertEmissionsTypeFormModel } from './monitoring-approach-form.provider';

export interface SimplifiedApproachFormModel {
  form: FormGroup<CertEmissionsTypeFormModel>;
  downloadUrl: string;
  submitHidden: boolean;
}
