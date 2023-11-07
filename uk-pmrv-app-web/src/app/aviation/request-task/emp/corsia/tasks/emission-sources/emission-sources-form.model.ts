import { FormArray, FormControl } from '@angular/forms';

import { AircraftTypeDetailsFormModelCorsia } from './aircraft-type/form/types';

export interface AdditionalAircraftMonitoringApproachModel {
  procedureDescription: FormControl<string>;
  procedureDocumentName: FormControl<string>;
  procedureReference: FormControl<string>;
  responsibleDepartmentOrRole: FormControl<string>;
  locationOfRecords: FormControl<string>;
  itSystemUsed: FormControl<string>;
}

export interface EmissionSourcesFormModelCorsia {
  aircraftTypes: FormArray<AircraftTypeDetailsFormModelCorsia>;
  multipleFuelConsumptionMethodsExplanation?: FormControl<string>;
}
