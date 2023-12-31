import {
  AviationAccountClosureSubmittedRequestActionPayload,
  AviationAerCorsiaApplicationCompletedRequestActionPayload,
  AviationAerCorsiaApplicationSubmittedRequestActionPayload,
  AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload,
  AviationAerUkEtsApplicationCompletedRequestActionPayload,
  AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload,
  AviationAerUkEtsApplicationSubmittedRequestActionPayload,
  AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload,
  AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload,
  AviationVirApplicationReviewedRequestActionPayload,
  AviationVirApplicationSubmittedRequestActionPayload,
  EmpIssuanceUkEtsApplicationApprovedRequestActionPayload,
  EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload,
  EmpVariationUkEtsApplicationApprovedRequestActionPayload,
  EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload,
  EmpVariationUkEtsApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

export type EmpRequestActionPayload = EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload &
  EmpIssuanceUkEtsApplicationApprovedRequestActionPayload &
  EmpVariationUkEtsApplicationSubmittedRequestActionPayload &
  EmpVariationUkEtsApplicationApprovedRequestActionPayload &
  EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;

export type AccountClosureRequestActionPayload = AviationAccountClosureSubmittedRequestActionPayload;

export type AerRequestActionPayload = AviationAerUkEtsApplicationSubmittedRequestActionPayload &
  AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload &
  AviationAerUkEtsApplicationCompletedRequestActionPayload &
  AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;

export type AerCorsiaRequestActionPayload = AviationAerCorsiaApplicationSubmittedRequestActionPayload &
  AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload &
  AviationAerCorsiaApplicationCompletedRequestActionPayload;

export type VirRequestActionPayload = AviationVirApplicationSubmittedRequestActionPayload &
  AviationVirApplicationReviewedRequestActionPayload &
  AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;
