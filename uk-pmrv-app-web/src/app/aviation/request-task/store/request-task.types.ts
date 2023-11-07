import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';

import {
  AviationAccountClosure,
  AviationAccountClosureSaveRequestTaskActionPayload,
  AviationAccountClosureSubmitRequestTaskPayload,
  AviationAerCorsia,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaSaveApplicationRequestTaskActionPayload,
  AviationAerCorsiaVerificationReport,
  AviationAerUkEts,
  AviationAerUkEtsApplicationReviewRequestTaskPayload,
  AviationAerUkEtsApplicationSubmitRequestTaskPayload,
  AviationAerUkEtsApplicationSubmittedRequestActionPayload,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
  AviationAerUkEtsSaveApplicationRequestTaskActionPayload,
  AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload,
  AviationAerUkEtsVerificationReport,
  AviationCorsiaOperatorDetails,
  AviationDreUkEtsApplicationSubmitRequestTaskPayload,
  AviationDreUkEtsSaveApplicationRequestTaskActionPayload,
  AviationOperatorDetails,
  AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AviationVirApplicationReviewRequestTaskPayload,
  AviationVirApplicationSubmitRequestTaskPayload,
  EmissionsMonitoringPlanCorsia,
  EmissionsMonitoringPlanUkEts,
  EmpIssuanceCorsiaApplicationReviewRequestTaskPayload,
  EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload,
  EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload,
  EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload,
  EmpIssuanceUkEtsApplicationReviewRequestTaskPayload,
  EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload,
  EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload,
  EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload,
  EmpVariationCorsiaApplicationReviewRequestTaskPayload,
  EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload,
  EmpVariationCorsiaApplicationSubmitRequestTaskPayload,
  EmpVariationCorsiaDetails,
  EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload,
  EmpVariationUkEtsApplicationReviewRequestTaskPayload,
  EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload,
  EmpVariationUkEtsApplicationSubmitRequestTaskPayload,
  EmpVariationUkEtsDetails,
  EmpVariationUkEtsRegulatorLedReason,
} from 'pmrv-api';

export type EmpRequestTaskPayload = EmpRequestTaskPayloadUkEts | EmpRequestTaskPayloadCorsia;

export type EmpRequestTaskPayloadUkEts = EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload &
  EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload &
  EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload &
  EmpIssuanceUkEtsApplicationReviewRequestTaskPayload &
  EmpVariationUkEtsApplicationSubmitRequestTaskPayload &
  EmpVariationUkEtsApplicationReviewRequestTaskPayload &
  EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload &
  EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;

export type EmpRequestTaskPayloadCorsia = EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload &
  EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload &
  EmpIssuanceCorsiaApplicationReviewRequestTaskPayload &
  EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload &
  EmpVariationCorsiaApplicationSubmitRequestTaskPayload &
  EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload &
  EmpVariationCorsiaApplicationReviewRequestTaskPayload;

export type EmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts | EmissionsMonitoringPlanCorsia;

export type EmpUkEtsTaskKey =
  | keyof EmissionsMonitoringPlanUkEts
  | 'submission'
  | 'serviceContactDetails'
  | 'decision'
  | 'empVariationDetails'
  | 'reasonRegulatorLed'
  | 'changesRequested';

export type EmpCorsiaTaskKey =
  | keyof EmissionsMonitoringPlanCorsia
  | 'submission'
  | 'serviceContactDetails'
  | 'decision'
  | 'empVariationDetails'
  | 'reasonRegulatorLed'
  | 'changesRequested';

export type EmpTaskKey = EmpUkEtsTaskKey | EmpCorsiaTaskKey;

export type EmpTask =
  | EmissionsMonitoringPlanUkEts[keyof EmissionsMonitoringPlanUkEts]
  | EmpRequestTaskPayloadCorsia[keyof EmpRequestTaskPayloadCorsia]
  | EmpRequestTaskPayloadUkEts['serviceContactDetails']
  | EmpVariationUkEtsDetails
  | EmpVariationUkEtsRegulatorLedReason
  | EmpVariationCorsiaDetails
  | EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload['reasonRegulatorLed'];

export type EmpCorsiaTask =
  | EmissionsMonitoringPlanCorsia[keyof EmissionsMonitoringPlanCorsia]
  | EmpRequestTaskPayloadCorsia['serviceContactDetails']
  | EmpVariationCorsiaDetails
  | EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload['reasonRegulatorLed'];

export type OperatorDetails = AviationOperatorDetails & AviationCorsiaOperatorDetails;

export type AerUkEtsRequestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload &
  AviationAerUkEtsSaveApplicationRequestTaskActionPayload &
  AviationAerUkEtsApplicationReviewRequestTaskPayload &
  AviationAerUkEtsApplicationSubmittedRequestActionPayload;

export type AerCorsiaRequestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload &
  AviationAerCorsiaSaveApplicationRequestTaskActionPayload;

export type AerRequestTaskPayload = AerUkEtsRequestTaskPayload & AerCorsiaRequestTaskPayload;

export type Aer = AviationAerUkEts;
export type AerCorsia = AviationAerCorsia;

export type AerTaskKey =
  | keyof Aer
  | keyof AerCorsia
  | 'totalEmissionsCorsia'
  | 'sendReport'
  | 'serviceContactDetails'
  | 'reportingObligation'
  | 'changesRequested'
  | 'confidentiality';

export type AerTask =
  | Aer[keyof Aer]
  | AerCorsia[keyof AerCorsia]
  | AerRequestTaskPayload['serviceContactDetails']
  | ReportingObligation;

export type AccountClosure = AviationAccountClosure;

export type AccountClosureTask = AviationAccountClosureSubmitRequestTaskPayload;

export type AccountClosureTaskPayload = AviationAccountClosureSubmitRequestTaskPayload &
  AviationAccountClosureSaveRequestTaskActionPayload;

export type DreRequestTaskPayload = AviationDreUkEtsApplicationSubmitRequestTaskPayload &
  AviationDreUkEtsSaveApplicationRequestTaskActionPayload;

export type Dre = AviationDreUkEtsApplicationSubmitRequestTaskPayload;

export type DreTaskKey = keyof Dre;

export type DreTask = Dre[keyof Dre] | ReportingObligation;

export type SectionsCompletedMap = { [key: string]: boolean[] };

export type SectionsCompleted = boolean | SectionsCompletedMap;

export type AerVerifyTaskPayload = AerVerify & AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;
export type AerVerifyCorsiaTaskPayload = AerVerifyCorsia;

export type AerVerify = AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
export type AerVerifyCorsia = AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;

export type AerVerifyTask =
  | AerVerify[keyof AerVerify]
  | AviationAerUkEtsVerificationReport[keyof AviationAerUkEtsVerificationReport];
export type AerVerifyCorsiaTask =
  | AerVerifyCorsia[keyof AerVerifyCorsia]
  | AviationAerCorsiaVerificationReport[keyof AviationAerCorsiaVerificationReport];

export type AerVerifyTaskKey = keyof AerVerify | keyof AviationAerUkEtsVerificationReport | 'sendReport';
export type AerVerifyCorsiaTaskKey = keyof AerVerifyCorsia | keyof AviationAerCorsiaVerificationReport | 'sendReport';

export type VirRequestTaskPayload = AviationVirApplicationSubmitRequestTaskPayload &
  AviationVirApplicationReviewRequestTaskPayload &
  AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
