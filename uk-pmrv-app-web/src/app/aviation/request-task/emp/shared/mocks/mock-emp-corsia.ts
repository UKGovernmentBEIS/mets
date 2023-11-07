import { EmissionsMonitoringPlanCorsia } from 'pmrv-api';

const PROCEDURE_FORM = {
  procedureDescription: 'Procedure description',
  procedureDocumentName: 'Name of the procedure document',
  procedureReference: 'Procedure reference',
  responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
  locationOfRecords: 'Location of records',
  itSystemUsed: 'IT system used',
};

const OPERATING_STATE_PAIRS = [
  {
    stateA: 'Canada',
    stateB: 'Mexico',
  },
];

export const EMP_CORSIA: EmissionsMonitoringPlanCorsia = {
  abbreviations: undefined,
  additionalDocuments: undefined,
  operatorDetails: undefined,
  emissionsMonitoringApproach: undefined,
  managementProcedures: undefined,
  emissionSources: undefined,
  flightAndAircraftProcedures: {
    aircraftUsedDetails: PROCEDURE_FORM,
    flightListCompletenessDetails: PROCEDURE_FORM,
    internationalFlightsDetermination: PROCEDURE_FORM,
    operatingStatePairs: { operatingStatePairsCorsiaDetails: OPERATING_STATE_PAIRS },
    internationalFlightsDeterminationOffset: PROCEDURE_FORM,
    internationalFlightsDeterminationNoMonitoring: PROCEDURE_FORM,
  },
  dataGaps: undefined,
};
