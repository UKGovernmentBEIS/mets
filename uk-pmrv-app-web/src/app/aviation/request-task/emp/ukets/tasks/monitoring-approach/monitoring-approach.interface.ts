import { FormGroup } from '@angular/forms';

export interface SimplifiedApproachViewModel {
  form: FormGroup;
  submitHidden: boolean;
  downloadUrl: string;
}
