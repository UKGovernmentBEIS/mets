import {
  EmpCorsiaOperatorDetails,
  EmpOperatorDetails
} from 'pmrv-api';

// temporary add any until corsia api models are ready
export type EmpIssuanceApplicationSubmitRequestTaskPayload = any;
// export type EmpIssuanceApplicationSubmitRequestTaskPayload = (
//   | EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload
//   | EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload
// )

export type EmpOperatorDetailsViewModel = EmpOperatorDetails | EmpCorsiaOperatorDetails;
