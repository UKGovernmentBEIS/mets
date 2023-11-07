import { FormControl, FormGroup } from '@angular/forms';

import { AviationAerDataGaps } from 'pmrv-api';

export interface AviationAerDataGapFormModel {
  reason: FormControl<string | null>;
  type: FormControl<string | null>;
  replacementMethod: FormControl<string | null>;
  flightsAffected: FormControl<number | null>;
  totalEmissions: FormControl<string | null>;
}

export interface AviationAerDataGapsFormModel {
  existGroup: FormGroup<{ exist: FormControl<AviationAerDataGaps['exist'] | null> }>;
  dataGapsGroup?: FormGroup<{ dataGaps: FormControl<AviationAerDataGaps['dataGaps'] | null> }>;
  affectedFlightsPercentage?: FormControl<AviationAerDataGaps['affectedFlightsPercentage'] | null>;
}

export interface DataGapsSummaryViewModel {
  pageHeader: string;
  isEditable: boolean;
  dataGaps: AviationAerDataGaps;
  hideSubmit: boolean;
  showDecision: boolean;
}
