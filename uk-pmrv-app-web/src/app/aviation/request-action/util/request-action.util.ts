import { AerCorsiaRequestActionReportComponent } from '@aviation/request-action/aer/corsia/report/aer-corsia-request-action-report.component';
import { AerUketsRequestActionReportComponent } from '@aviation/request-action/aer/ukets/report/aer-ukets-request-action-report.component';
import { getEmpApplicationSubmittedTasks } from '@aviation/request-action/emp/util/emp.util';
import {
  AerCorsiaRequestActionPayload,
  AerUkEtsRequestActionPayload,
  EmpRequestActionPayload,
  VirRequestActionPayload,
} from '@aviation/request-action/store';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { RequestActionDTO, RequestActionPayload } from 'pmrv-api';

import {
  getAerCorsiaApplicationSubmittedTasks,
  getAerCorsiaCompletedTasks,
  getAerCorsiaVerifyApplicationSubmittedTasks,
} from '../aer/corsia/util/aer-corsia.utils';
import {
  getAerApplicationSubmittedTasks,
  getAerUkEtsCompletedTasks,
  getAerVerifyApplicationSubmittedTasks,
} from '../aer/ukets/util/aer-ukets.utils';

export const variationRegulatorLedaApprovedRequestActionTypes: RequestActionDTO['type'][] = [
  'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED',
  'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED',
];

export const reportableRequestActionTypes: RequestActionDTO['type'][] = [
  'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
  'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER',
  'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
  'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED',
  'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER',
  'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER',
  'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
];

export const reportableAerUketsRequestActionTypes: RequestActionDTO['type'][] = [
  'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER',
  'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
  'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED',
  'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER',
  'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
];

export const reportableAerCorsiaRequestActionTypes: RequestActionDTO['type'][] = [
  'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED',
  'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER',
  'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
];

export const reportComponentRequestActionTypes: {
  reportComponent: any;
  requestActionTypes: RequestActionDTO['type'][];
}[] = [
  { reportComponent: AerUketsRequestActionReportComponent, requestActionTypes: reportableAerUketsRequestActionTypes },
  { reportComponent: AerCorsiaRequestActionReportComponent, requestActionTypes: reportableAerCorsiaRequestActionTypes },
];

export function getRequestActionHeader(type: RequestActionDTO['type'], payload?: RequestActionPayload): string {
  switch (type) {
    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED':
      return 'Apply to vary your emissions monitoring plan';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED':
      return 'Changes submitted';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
    case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
    case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
      return 'Approved';

    case 'EMP_VARIATION_UKETS_APPLICATION_REJECTED':
      return 'Rejected';

    case 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
    case 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN':
      return 'Withdrawn';

    case 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED':
      return 'Account closed';

    case 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER':
    case 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED':
    case 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER':
    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED':
      return (payload as AerUkEtsRequestActionPayload).reportingYear + ' emissions report submitted';

    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED':
    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED':
      return (payload as AerUkEtsRequestActionPayload).reportingYear + ' emissions report verification submitted';

    case 'AVIATION_AER_UKETS_APPLICATION_COMPLETED':
    case 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED':
      return (payload as AerUkEtsRequestActionPayload).reportingYear + ' emissions report reviewed';
    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED':
    case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED':
      return (payload as AerUkEtsRequestActionPayload).reportingYear + ' completed without review';

    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER':
    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED':
      return 'Changes submitted';

    case 'AVIATION_VIR_APPLICATION_SUBMITTED':
      return (payload as VirRequestActionPayload).reportingYear + ' verifier improvement report submitted';

    case 'AVIATION_VIR_APPLICATION_REVIEWED':
      return (payload as VirRequestActionPayload).reportingYear + ' verifier improvement report decision submitted';

    case 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
      return (
        'Follow up response to ' +
        ((payload as VirRequestActionPayload).verifierUncorrectedItem?.reference ??
          (payload as VirRequestActionPayload).verifierComment?.reference)
      );

    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED':
      return 'Annual offsetting requirements submitted';

    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED':
      return '3 year period offsetting requirements submitted';

    case 'AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR':
    case 'AVIATION_AER_CORSIA_VERIFICATION_RETURNED_TO_OPERATOR':
      return 'Verifier returned to operator for changes';

    default:
      return '';
  }
}

export function getApplicationSubmittedTasks(
  type: RequestActionDTO['type'],
  payload: RequestActionPayload,
  regulatorViewer: boolean,
  isCorsia: boolean,
): TaskSection<any>[] {
  switch (type) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED':
    case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
    case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED':
    case 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN':
    case 'EMP_VARIATION_UKETS_APPLICATION_REJECTED':
    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED':
      return getEmpApplicationSubmittedTasks(payload as EmpRequestActionPayload, regulatorViewer, isCorsia);

    case 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER':
    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER':
    case 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED':
    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED':
      return getAerApplicationSubmittedTasks(payload as AerUkEtsRequestActionPayload);

    case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED':
      return getAerVerifyApplicationSubmittedTasks(payload as AerUkEtsRequestActionPayload);

    case 'AVIATION_AER_UKETS_APPLICATION_COMPLETED':
    case 'AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED':
      return getAerUkEtsCompletedTasks(payload as AerUkEtsRequestActionPayload);

    case 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER':
    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED':
      return getAerCorsiaApplicationSubmittedTasks(payload as AerCorsiaRequestActionPayload);
    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED':
      return getAerCorsiaVerifyApplicationSubmittedTasks(payload as AerCorsiaRequestActionPayload);
    case 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED':
    case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED':
      return getAerCorsiaCompletedTasks(payload as AerCorsiaRequestActionPayload);

    default:
      return [];
  }
}

export const peerReviewRequestActionTypes = [
  'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED',
  'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED',
  'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED',
  'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED',
  'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED',
  'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED',
  'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED',
  'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED',
  'AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED',
  'AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED',
  'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED',
  'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED',
  'AVIATION_DOE_CORSIA_PEER_REVIEWER_ACCEPTED',
  'AVIATION_DOE_CORSIA_PEER_REVIEWER_REJECTED',
];
