import { FormArray, FormControl, FormGroup } from '@angular/forms';

import { AircraftTypeDetailsFormModel } from './aircraft-type/form/types';

export interface AdditionalAircraftMonitoringApproachModel {
  procedureDescription: FormControl<string>;
  procedureDocumentName: FormControl<string>;
  procedureReference: FormControl<string>;
  responsibleDepartmentOrRole: FormControl<string>;
  locationOfRecords: FormControl<string>;
  itSystemUsed: FormControl<string>;
}

export interface EmissionSourcesFormModel {
  aircraftTypes: FormArray<AircraftTypeDetailsFormModel>;
  otherFuelExplanation?: FormControl<string | null>;
  multipleFuelConsumptionMethodsExplanation?: FormControl<string>;
  additionalAircraftMonitoringApproach?: FormGroup<AdditionalAircraftMonitoringApproachModel>;
}
