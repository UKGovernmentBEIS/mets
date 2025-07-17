import {
  PermanentCessationApplicationSubmittedRequestActionPayload,
  PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload,
} from 'pmrv-api';

export type recipientsPayloadType = Pick<
  PermanentCessationApplicationSubmittedRequestActionPayload &
    PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload,
  'usersInfo' | 'decisionNotification' | 'reviewDecisionNotification' | 'officialNotice'
>;
