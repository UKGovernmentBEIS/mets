import { FormControl, FormGroup } from '@angular/forms';

export type DataGapsFormModel = FormGroup<{
  dataGaps: FormControl<string>;
  secondaryDataSources: FormControl<string>;
  substituteData: FormControl<string>;
  otherDataGapsTypes: FormControl<string>;
}>;
