import { FormControl, FormGroup } from '@angular/forms';

import { EmpFlightAndAircraftProceduresCorsia } from 'pmrv-api';

export interface EmpFlightAndAircraftProceduresCorsiaFormModel {
  aircraftUsedDetails: FormGroup<EmpProcedureFormFormModel>;
  flightListCompletenessDetails: FormGroup<EmpProcedureFormFormModel>;
  internationalFlightsDetermination: FormGroup<EmpProcedureFormFormModel>;
  internationalFlightsDeterminationOffset: FormGroup<EmpProcedureFormFormModel>;
  internationalFlightsDeterminationNoMonitoring: FormGroup<EmpProcedureFormFormModel>;

  operatingStatePairs: FormGroup<EmpOperatingStatePairsCorsiaFormModel>;
}

export interface EmpProcedureFormFormModel {
  procedureDescription: FormControl<string>;
  procedureDocumentName: FormControl<string>;
  procedureReference: FormControl<string>;
  responsibleDepartmentOrRole: FormControl<string>;
  locationOfRecords: FormControl<string>;
  itSystemUsed?: FormControl<string>;
}

export interface EmpOperatingStatePairsCorsiaFormModel {
  operatingStatePairsCorsiaDetails: FormControl<EmpOperatingStatePairsCorsiaDetails[]>;
}

export interface EmpOperatingStatePairsCorsiaDetails {
  stateA: FormControl<string>;
  stateB: FormControl<string>;
}

export interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  showDecision: boolean;
  data: EmpFlightAndAircraftProceduresCorsia;
  hideSubmit: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpFlightAndAircraftProceduresCorsia;
}
