import { FormGroup } from '@angular/forms';

import { getAerCorsiaReviewSections } from '@aviation/request-task/aer/corsia/aer-review/util/aer-review-corsia.util';
import { getAerVerifyCorsiaSections } from '@aviation/request-task/aer/corsia/aer-verify/util/aer-verify-corsia.util';
import {
  aerHeaderTaskMap,
  getAerReviewSections,
  getAerSections,
} from '@aviation/request-task/aer/shared/util/aer.util';
import { getAerVerifySections } from '@aviation/request-task/aer/ukets/aer-verify/util/aer-verify.util';
import { ReviewDeterminationStatus } from '@permit-application/review/types/review.permit.type';
import { TaskSection } from '@shared/task-list/task-list.interface';
import Papa from 'papaparse';

import {
  AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerRequestMetadata,
  AviationAerUkEtsApplicationReviewRequestTaskPayload,
  AviationAerUkEtsApplicationSubmitRequestTaskPayload,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
  AviationDoECorsiaApplicationSubmitRequestTaskPayload,
  AviationDoECorsiaRequestMetadata,
  AviationDreUkEtsApplicationSubmitRequestTaskPayload,
  AviationVirRequestMetadata,
  EmpIssuanceCorsiaApplicationReviewRequestTaskPayload,
  EmpIssuanceUkEtsApplicationReviewRequestTaskPayload,
  EmpVariationCorsiaApplicationReviewRequestTaskPayload,
  EmpVariationDetermination,
  EmpVariationUkEtsApplicationReviewRequestTaskPayload,
  ItemDTO,
  NonComplianceApplicationSubmitRequestTaskPayload,
  NonComplianceCivilPenaltyRequestTaskPayload,
  NonComplianceDailyPenaltyNoticeRequestTaskPayload,
  NonComplianceFinalDeterminationRequestTaskPayload,
  NonComplianceNoticeOfIntentRequestTaskPayload,
  RequestInfoDTO,
  RequestMetadata,
  RequestTaskActionProcessDTO,
  RequestTaskAttachmentActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { aerVerifyHeaderTaskMap } from '../aer/shared/util/aer-verify-tasks.util';
import {
  areTasksCompletedForNotify3YearPeriodOffsetting,
  getCorsia3YearOffsettingSections,
  threeYearPeriodOffsettingDocumentPreviewRequestTaskActionTypesMap,
} from '../aer-corsia-3year-period-offsetting/util/3year-period-offsetting.util';
import { getPreviewDocumentsInfo3YearPeriodOffsetting } from '../aer-corsia-3year-period-offsetting/util/previewDocuments3YearPeriodOffsetting.util';
import {
  annualOffsettingDocumentPreviewRequestTaskActionTypesMap,
  areTasksCompletedForNotifyAnnualOffsetting,
  getCorsiaAnnualOffsettingSections,
} from '../aer-corsia-annual-offsetting/util/annual-offsetting.util';
import { getPreviewDocumentsInfoAnnualOffsetting } from '../aer-corsia-annual-offsetting/util/previewDocumentsAnnualOffsetting.util';
import { doeDocumentPreviewRequestTaskActionTypesMap, getDoeSections } from '../doe/utils/doe.utils';
import { getPreviewDocumentsInfoDoe } from '../doe/utils/previewDocuments-doe.util';
import { DREdocumentPreviewRequestTaskActionTypesMap, getDreSections } from '../dre/util/dre.util';
import { getPreviewDocumentsInfoDre } from '../dre/util/previewDocuments-dre.util';
import {
  areTasksCompletedForNotifyVariationRegLed,
  documentPreviewRequestTaskActionTypesMap,
  empHeaderTaskMap,
  getEmpCorsiaReviewSections,
  getEmpSections,
  getEmpUkEtsReviewSections,
  getEmpVariationReviewSections,
  variationOperatorLedReviewRequestTaskTypes,
} from '../emp/shared/util/emp.util';
import { getPreviewDocumentsInfoEmp } from '../emp/shared/util/previewDocumentsEmp.util';
import {
  getNonComplianceApplicationSections,
  getNonComplianceCivilPenaltytPeerReviewSections,
  getNonComplianceCivilPenaltytPeerReviewWaitSections,
  getNonComplianceCivilPenaltytSections,
  getNonComplianceDailyPenaltyPeerReviewSections,
  getNonComplianceDailyPenaltyPeerReviewWaitSections,
  getNonComplianceDailyPenaltySections,
  getNonComplianceFinalDeterminationSections,
  getNonComplianceNoticeOfIntentPeerReviewSections,
  getNonComplianceNoticeOfIntentPeerReviewWaitSections,
  getNonComplianceNoticeOfIntentSections,
} from '../non-compliance/non-compliance.util';
import {
  AerCorsiaAnnualOffsettingPayload,
  AerTaskKey,
  AerVerifyTaskKey,
  Doe,
  DoeTaskKey,
  Dre,
  DreTaskKey,
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  EmpTaskKey,
} from '../store';

export const CorsiaRequestTypes: RequestInfoDTO['type'][] = [
  'EMP_ISSUANCE_CORSIA',
  'EMP_VARIATION_CORSIA',
  'AVIATION_AER_CORSIA',
  'AVIATION_DOE_CORSIA',
];

export function getRequestTaskHeaderForTaskType(type: RequestTaskDTO['type'], metadata: RequestMetadata): string {
  switch (type) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
      return 'Apply for an emissions monitoring plan';

    case 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT':
      return 'Provide details of breach: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'Upload initial penalty notice: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
      return 'Initial penalty notice sent to peer reviewer: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return 'Peer review initial penalty: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'Upload notice of intent: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return 'Peer review notice of intent: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
      return 'Notice of intent sent to peer reviewer: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return 'Upload penalty notice: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
      return 'Penalty sent to peer reviewer: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return 'Peer review upload penalty: non-compliance';
    case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
      return 'Provide conclusion: non-compliance';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
      return 'Review emissions monitoring plan application';

    case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
      return 'Complete ' + (metadata as AviationAerRequestMetadata).year + ' emissions report';

    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
    case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
      return 'Review ' + (metadata as AviationAerRequestMetadata).year + ' emissions report';

    case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
      return `Verify ${(metadata as AviationAerRequestMetadata).year} emissions report`;

    case 'AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION':
      return (metadata as AviationAerRequestMetadata).year + ' emissions report sent to verifier';

    case 'AVIATION_AER_UKETS_WAIT_FOR_REVIEW':
    case 'AVIATION_AER_CORSIA_WAIT_FOR_REVIEW':
      return (metadata as AviationAerRequestMetadata).year + ' emissions report';

    case 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS':
    case 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS':
      return (metadata as AviationAerRequestMetadata).year + ' emissions report';

    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'Amend your ' + (metadata as AviationAerRequestMetadata).year + ' emissions report';

    case 'EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW':
      return 'Emissions monitoring plan application sent to regulator';

    case 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS':
    case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS':
      return 'Emissions monitoring plan application returned to operator';

    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
      return 'Apply to vary your emissions monitoring plan';

    case 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW':
      return 'Emissions monitoring plan application sent to peer reviewer';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'Amend your emissions monitoring plan';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
      return 'Peer review emissions monitoring plan application';

    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return 'Review emissions monitoring plan variation';

    case 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW':
    case 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW':
      return 'Emissions monitoring plan variation sent to regulator';

    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'Amend your emissions monitoring plan variation';

    case 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS':
      return 'Emissions monitoring plan variation returned to operator';

    case 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS':
      return 'Emissions monitoring plan variation returned to operator';

    case 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW':
      return 'Emissions monitoring plan variation sent to peer reviewer';

    case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
      return 'Peer review emissions monitoring plan variation';

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'Vary the emissions monitoring plan';

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
      return 'Vary the emissions monitoring plan sent to peer reviewer';

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'Peer review vary the emissions monitoring plan';

    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
      return `Determine ${(metadata as AviationAerRequestMetadata).year} emissions`;

    case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
      return `Peer review ${(metadata as AviationAerRequestMetadata).year} emissions determination`;

    case 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW':
      return (
        (metadata as AviationAerRequestMetadata).year + ' emissions determination application sent to peer reviewer'
      );

    case 'AVIATION_VIR_APPLICATION_SUBMIT':
    case 'AVIATION_VIR_WAIT_FOR_REVIEW':
    case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS':
      return (metadata as AviationVirRequestMetadata).year + ' verifier improvement report';
    case 'AVIATION_VIR_APPLICATION_REVIEW':
      return 'Review ' + (metadata as AviationVirRequestMetadata).year + ' verifier improvement report';

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
      return 'Calculate annual offsetting requirements';
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
      return 'Peer review annual offsetting requirements';
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW':
      return 'Sent to peer reviewer annual offsetting requirements';

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
      return 'Calculate 3-year period offsetting requirements';
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
      return 'Peer review 3-year period offsetting requirements';
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW':
      return 'Sent to peer reviewer 3-year period offsetting requirements';

    case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
      return `Estimate ${(metadata as AviationDoECorsiaRequestMetadata).year} emissions`;
    case 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW':
      return (
        (metadata as AviationDoECorsiaRequestMetadata).year + ' emissions estimation application sent to peer reviewer'
      );
    case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
      return `Peer review ${(metadata as AviationDoECorsiaRequestMetadata).year} emissions estimation`;

    default:
      return '';
  }
}

export function getMessageForTaskType(type: RequestTaskDTO['type']): string {
  switch (type) {
    case 'AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION':
    case 'AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION':
      return 'Waiting for the verifier to complete the opinion statement.';

    case 'AVIATION_AER_UKETS_WAIT_FOR_REVIEW':
    case 'AVIATION_AER_CORSIA_WAIT_FOR_REVIEW':
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW':
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW':
      return 'Waiting for the regulator to complete the review';

    default:
      return null;
  }
}

export function getSummaryHeaderForTaskType(
  type: RequestTaskDTO['type'],
  taskName: EmpTaskKey | AerTaskKey | DreTaskKey | AerVerifyTaskKey | DoeTaskKey,
): string {
  switch (type) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return empHeaderTaskMap[taskName];

    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
      return aerHeaderTaskMap[taskName] ?? aerVerifyHeaderTaskMap[taskName];
    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return aerHeaderTaskMap[taskName] ?? 'Check your answers';
    default:
      return 'Check your answers';
  }
}

export function getSaveRequestTaskActionTypeForRequestTaskType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
      return 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION';

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
      return 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW';

    case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
      return 'AVIATION_AER_UKETS_SAVE_APPLICATION';

    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
      return 'AVIATION_AER_CORSIA_SAVE_APPLICATION';

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW';

    case 'AVIATION_ACCOUNT_CLOSURE_SUBMIT':
      return 'AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION';

    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UKETS_SAVE_APPLICATION';

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED';

    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
      return 'AVIATION_DRE_UKETS_SAVE_APPLICATION';

    case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
      return 'AVIATION_DOE_CORSIA_SUBMIT_SAVE';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
      return 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND';

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND';

    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return 'EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW';

    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
      return 'EMP_VARIATION_UKETS_SAVE_APPLICATION_AMEND';

    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND';

    case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
      return 'AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION';

    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION';

    case 'AVIATION_VIR_APPLICATION_SUBMIT':
      return 'AVIATION_VIR_SAVE_APPLICATION';

    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
      return 'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION';

    case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
      return 'AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION';

    case 'AVIATION_VIR_APPLICATION_REVIEW':
      return 'AVIATION_VIR_SAVE_REVIEW';

    case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS':
      return 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS';

    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_CORSIA_SAVE_APPLICATION';

    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED';

    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
      return 'AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND';

    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND';

    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW';

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
      return 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_APPLICATION';

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
      return 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SAVE_APPLICATION' as any;
  }
}

export function getRequestTaskAttachmentTypeForRequestTaskType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskAttachmentActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT';

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_UPLOAD_SECTION_ATTACHMENT';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
      return 'EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT';

    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return 'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT';

    case 'AVIATION_VIR_APPLICATION_SUBMIT':
      return 'AVIATION_VIR_UPLOAD_ATTACHMENT';
  }
}

export function getSectionsForTaskType(
  requestTaskType: RequestTaskDTO['type'],
  requestType: RequestInfoDTO['type'],
  payload: RequestTaskPayload,
  relatedActions?: RequestTaskItemDTO['allowedRequestTaskActions'],
): TaskSection<any>[] {
  const isAmendsTask = amendsRequestedTaskActionTypes.includes(requestTaskType);
  const isCorsia = CorsiaRequestTypes.includes(requestType);
  switch (requestTaskType) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
    case 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW':
    case 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
      return getEmpUkEtsReviewSections(payload as EmpIssuanceUkEtsApplicationReviewRequestTaskPayload);

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS':
    case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
      return getEmpCorsiaReviewSections(payload as EmpIssuanceCorsiaApplicationReviewRequestTaskPayload);

    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return getEmpSections(payload, requestTaskType, isAmendsTask, relatedActions, isCorsia);

    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS':
    case 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS':
    case 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
      return getEmpVariationReviewSections(
        payload as
          | EmpVariationUkEtsApplicationReviewRequestTaskPayload
          | EmpVariationCorsiaApplicationReviewRequestTaskPayload,
        isCorsia,
      );

    case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
      return getAerSections(payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload, isAmendsTask);

    case 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS':
    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
      return getAerReviewSections(payload as AviationAerUkEtsApplicationReviewRequestTaskPayload);

    case 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS':
    case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
      return getAerCorsiaReviewSections(payload);

    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
      return getAerSections(
        payload as
          | AviationAerCorsiaApplicationSubmitRequestTaskPayload
          | AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
        isAmendsTask,
        true,
      );

    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return getAerVerifySections(payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload);

    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return getAerVerifyCorsiaSections(payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload);

    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
    case 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW':
    case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
      return getDreSections(payload as AviationDreUkEtsApplicationSubmitRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT':
      return getNonComplianceApplicationSections(payload as NonComplianceApplicationSubmitRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return getNonComplianceDailyPenaltySections(payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
      return getNonComplianceDailyPenaltyPeerReviewWaitSections(
        payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload,
      );

    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return getNonComplianceDailyPenaltyPeerReviewSections(
        payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload,
      );

    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return getNonComplianceNoticeOfIntentSections(payload as NonComplianceNoticeOfIntentRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
      return getNonComplianceNoticeOfIntentPeerReviewWaitSections(
        payload as NonComplianceNoticeOfIntentRequestTaskPayload,
      );

    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return getNonComplianceNoticeOfIntentPeerReviewSections(payload as NonComplianceNoticeOfIntentRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return getNonComplianceCivilPenaltytSections(payload as NonComplianceCivilPenaltyRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
      return getNonComplianceCivilPenaltytPeerReviewWaitSections(
        payload as NonComplianceCivilPenaltyRequestTaskPayload,
      );

    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return getNonComplianceCivilPenaltytPeerReviewSections(payload as NonComplianceCivilPenaltyRequestTaskPayload);

    case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
      return getNonComplianceFinalDeterminationSections(payload as NonComplianceFinalDeterminationRequestTaskPayload);

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
      return getCorsiaAnnualOffsettingSections(payload as AerCorsiaAnnualOffsettingPayload);

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
      return getCorsia3YearOffsettingSections(payload);

    case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
    case 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW':
    case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
      return getDoeSections(payload as AviationDoECorsiaApplicationSubmitRequestTaskPayload);

    default:
      return [];
  }
}

export function getSubtaskSummaryValues(form: FormGroup): any {
  if (!form || !form.controls) {
    return null;
  }

  return Object.keys(form.controls)
    .map((controlKey) => ({
      [controlKey]: form.controls[controlKey].valid ? form.controls[controlKey].getRawValue() : null,
    }))
    .reduce((prev, cur) => ({ ...prev, ...cur }), {});
}

export const parseCsv = (value: string, skipEmptyLines = false): string[] => {
  return Papa.parse(value || '', { skipEmptyLines }).data.map((data: string[]) => {
    return data.join(',');
  });
};

export const unparseCsv = (value: string[]): string => {
  const valueTransformed = (value || []).map((x) => (x || '').split(','));
  return Papa.unparse(valueTransformed);
};

export const notifyOperatorRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
  'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
  'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR',
  'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
  'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
  'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
  'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
  'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR',
  'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR',
  'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR',
  'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR',
];

export const peerReviewRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW',
  'AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW',
  'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW',
  'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED',
  'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_REGULATOR_LED',
  'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW',
  'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW',
  'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PEER_REVIEW',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PEER_REVIEW',
  'AVIATION_DOE_CORSIA_REQUEST_PEER_REVIEW',
];

export const returnForAmendsRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS',
  'EMP_ISSUANCE_CORSIA_REVIEW_RETURN_FOR_AMENDS',
  'EMP_VARIATION_UKETS_REVIEW_RETURN_FOR_AMENDS',
  'EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS',
  'AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS',
  'AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS',
];

export const startPeerReviewRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'EMP_ISSUANCE_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'EMP_ISSUANCE_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED',
  'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED',
  'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION',
  'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION',
  'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION',
  'NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION',
  'AVIATION_DOE_CORSIA_SUBMIT_PEER_REVIEW_DECISION',
];

export const isPaymentRequired: Array<ItemDTO['taskType']> = [
  'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
  'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
  'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
];

export const reviewButtonsVisible: Array<ItemDTO['taskType']> = [
  'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
  'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
  'AVIATION_DRE_UKETS_APPLICATION_SUBMIT',
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
  'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
  'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE',
  'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT',
  'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY',
  'AVIATION_AER_UKETS_APPLICATION_REVIEW',
  'AVIATION_AER_CORSIA_APPLICATION_REVIEW',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT',
  'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
];

export const notifyOperatorUrlMapper: Partial<Record<RequestTaskDTO['type'], string[]>> = {
  EMP_ISSUANCE_UKETS_APPLICATION_REVIEW: ['emp', 'review', 'notify-operator'],
  EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'review', 'notify-operator'],
  AVIATION_DRE_UKETS_APPLICATION_SUBMIT: ['dre', 'review', 'notify-operator'],
  EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT: ['emp', 'variation-regulator-led', 'notify-operator'],
  EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT: ['emp-corsia', 'variation-regulator-led', 'notify-operator'],
  EMP_VARIATION_UKETS_APPLICATION_REVIEW: ['emp', 'variation', 'review', 'notify-operator'],
  EMP_VARIATION_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'variation', 'review', 'notify-operator'],
  AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE: ['non-compliance', 'daily-penalty-notice', 'notify-operator'],
  AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT: ['non-compliance', 'notice-of-intent', 'notify-operator'],
  AVIATION_NON_COMPLIANCE_CIVIL_PENALTY: ['non-compliance', 'civil-penalty-notice', 'notify-operator'],
  AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT: ['annual-offsetting-requirements', 'notify-operator'],
  AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT: ['3year-offsetting-requirements', 'notify-operator'],
  AVIATION_DOE_CORSIA_APPLICATION_SUBMIT: ['doe', 'review', 'notify-operator'],
};

export const sendForPeerReviewUrlMapper: Partial<Record<RequestTaskDTO['type'], string[]>> = {
  EMP_ISSUANCE_UKETS_APPLICATION_REVIEW: ['emp', 'review', 'peer-review'],
  EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'review', 'peer-review'],
  EMP_VARIATION_UKETS_APPLICATION_REVIEW: ['emp', 'variation', 'review', 'peer-review'],
  EMP_VARIATION_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'variation', 'review', 'peer-review'],
  EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT: ['emp', 'variation-regulator-led', 'peer-review'],
  EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT: ['emp-corsia', 'variation-regulator-led', 'peer-review'],
  AVIATION_DRE_UKETS_APPLICATION_SUBMIT: ['dre', 'review', 'peer-review'],
  AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE: ['non-compliance', 'daily-penalty-notice', 'peer-review'],
  AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT: ['non-compliance', 'notice-of-intent', 'peer-review'],
  AVIATION_NON_COMPLIANCE_CIVIL_PENALTY: ['non-compliance', 'civil-penalty-notice', 'peer-review'],
  AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT: ['annual-offsetting-requirements', 'peer-review'],
  AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT: ['3year-offsetting-requirements', 'peer-review'],
  AVIATION_DOE_CORSIA_APPLICATION_SUBMIT: ['doe', 'review', 'peer-review'],
};

export const returnForAmendsUrlMapper: Partial<Record<RequestTaskDTO['type'], string[]>> = {
  EMP_ISSUANCE_UKETS_APPLICATION_REVIEW: ['emp', 'review', 'return-for-amends'],
  EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'review', 'return-for-amends'],
  EMP_VARIATION_UKETS_APPLICATION_REVIEW: ['emp', 'variation', 'review', 'return-for-amends'],
  EMP_VARIATION_CORSIA_APPLICATION_REVIEW: ['emp-corsia', 'variation', 'review', 'return-for-amends'],
  AVIATION_AER_UKETS_APPLICATION_REVIEW: ['aer', 'review', 'return-for-amends'],
  AVIATION_AER_CORSIA_APPLICATION_REVIEW: ['aer-review-corsia', 'return-for-amends'],
};

export const isDeterminationCompleted = (
  requestTaskType: RequestTaskDTO['type'],
  requestType: RequestInfoDTO['type'],
  payload: RequestTaskPayload,
) => {
  switch (requestTaskType) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return (payload as EmpRequestTaskPayloadUkEts).reviewSectionsCompleted?.decision;

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return (payload as EmpRequestTaskPayloadCorsia).reviewSectionsCompleted?.decision;

    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
      return (payload as Dre).sectionCompleted;

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT': {
      const isCorsia = CorsiaRequestTypes.includes(requestType);
      return areTasksCompletedForNotifyVariationRegLed(requestTaskType, payload, isCorsia);
    }

    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return (payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload).dailyPenaltyCompleted;

    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return (payload as NonComplianceNoticeOfIntentRequestTaskPayload).noticeOfIntentCompleted;

    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return (payload as NonComplianceCivilPenaltyRequestTaskPayload).civilPenaltyCompleted;

    case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
      return (payload as NonComplianceFinalDeterminationRequestTaskPayload).determinationCompleted;

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
      return areTasksCompletedForNotifyAnnualOffsetting(payload as AerCorsiaAnnualOffsettingPayload);

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
      return areTasksCompletedForNotify3YearPeriodOffsetting(payload);

    case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
      return (payload as Doe).sectionCompleted;

    default:
      return false;
  }
};

export const isAnySectionForAmends = (payload: any, requestTaskType: RequestTaskDTO['type']) => {
  return Object.keys(payload?.reviewGroupDecisions ?? {}).some(
    (key) =>
      (payload.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' && payload.reviewSectionsCompleted[key]) ||
      (variationOperatorLedReviewRequestTaskTypes.includes(requestTaskType) &&
        variationDetailsSectionIsForAmends(payload)),
  );
};

export const isNotEditableByRequestTaskType: Array<RequestTaskDTO['type']> = [
  'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW',
  'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS',
  'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS',
  'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS',
  'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS',
  'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW',
  'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW',
  'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW',
  'AVIATION_AER_UKETS_APPLICATION_REVIEW',
  'AVIATION_AER_CORSIA_APPLICATION_REVIEW',
  'AVIATION_AER_UKETS_WAIT_FOR_AMENDS',
  'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS',
  'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW',
  'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW',
  'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW',
  'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW',
  'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW',
  'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW',
  'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW',
  'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW',
];

export const showReviewDecisionComponent: Array<RequestTaskDTO['type']> = [
  'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
  'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW',
  'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW',
  'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
  'EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW',
  'AVIATION_AER_UKETS_APPLICATION_REVIEW',
  'AVIATION_AER_UKETS_WAIT_FOR_AMENDS',
  'AVIATION_AER_CORSIA_APPLICATION_REVIEW',
  'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS',
];

export const showVariationReviewDecisionComponent: Array<RequestTaskDTO['type']> = [
  'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
  'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
  'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW',
];

export const showVariationRegLedDecisionComponent: Array<RequestTaskDTO['type']> = [
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
  'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW',
];

export const amendsRequestedTaskActionTypes: Array<RequestTaskDTO['type'] | null> = [
  'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT',
  'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT',
  'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT',
  'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT',
  'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT',
  'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT',
];

export const overallDecisionConfirmUrlMapper: Partial<Record<ItemDTO['requestType'], string[]>> = {
  EMP_ISSUANCE_UKETS: ['../../../../'],
  EMP_ISSUANCE_CORSIA: ['../../../../'],
  EMP_VARIATION_UKETS: ['../../../../../'],
  EMP_VARIATION_CORSIA: ['../../../../../'],
};

export const sendReturnForAmendsRequestTaskActionTypesMapper: Partial<
  Record<ItemDTO['taskType'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  EMP_ISSUANCE_UKETS_APPLICATION_REVIEW: 'EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS',
  EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW: 'EMP_ISSUANCE_CORSIA_REVIEW_RETURN_FOR_AMENDS',
  EMP_VARIATION_UKETS_APPLICATION_REVIEW: 'EMP_VARIATION_UKETS_REVIEW_RETURN_FOR_AMENDS',
  EMP_VARIATION_CORSIA_APPLICATION_REVIEW: 'EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS',
  AVIATION_AER_UKETS_APPLICATION_REVIEW: 'AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS',
  AVIATION_AER_CORSIA_APPLICATION_REVIEW: 'AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS',
};

export const recallFromAmendsTaskActionTypes: RequestTaskItemDTO['allowedRequestTaskActions'] = [
  'EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS',
  'EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS',
  'EMP_VARIATION_UKETS_RECALL_FROM_AMENDS',
  'EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS',
];

export const recallFromAmendsSubmitTaskActionTypes: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS: 'EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS',
  EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS: 'EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS',
  EMP_VARIATION_UKETS_WAIT_FOR_AMENDS: 'EMP_VARIATION_UKETS_RECALL_FROM_AMENDS',
  EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS: 'EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS',
};

export const variationDetailsSectionIsForAmends = (payload: any): boolean => {
  return (
    payload.empVariationDetailsReviewCompleted &&
    payload.empVariationDetailsReviewDecision.type === 'OPERATOR_AMENDS_NEEDED'
  );
};

export const notDisplayDiffComponent: Array<RequestTaskPayload['payloadType']> = [
  'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
  'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
];

const getDeterminationMap = (determinationType: EmpVariationDetermination['type']): string => {
  switch (determinationType) {
    case 'APPROVED':
      return 'approved';

    case 'REJECTED':
      return 'rejected';

    case 'DEEMED_WITHDRAWN':
      return 'withdrawn';
  }
};

export const getPreviewDocumentsInfo = (requestTaskType: RequestTaskDTO['type'], payload: any) => {
  switch (requestTaskType) {
    case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
      return getPreviewDocumentsInfoDre(DREdocumentPreviewRequestTaskActionTypesMap(requestTaskType));

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
      return getPreviewDocumentsInfoAnnualOffsetting(
        annualOffsettingDocumentPreviewRequestTaskActionTypesMap(requestTaskType),
      );

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
      return getPreviewDocumentsInfo3YearPeriodOffsetting(
        threeYearPeriodOffsettingDocumentPreviewRequestTaskActionTypesMap(requestTaskType),
      );

    case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
      return getPreviewDocumentsInfoDoe(doeDocumentPreviewRequestTaskActionTypesMap(requestTaskType));

    default:
      return getPreviewDocumentsInfoEmp(
        documentPreviewRequestTaskActionTypesMap(requestTaskType),
        getDeterminationMap(payload?.determination?.type) as ReviewDeterminationStatus,
      );
  }
};
