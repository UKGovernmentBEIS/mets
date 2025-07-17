import { CommonActionsState } from '@actions/store/common-actions.state';

import {
  CaExternalContactsDTO,
  NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

export const mockPayload: NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD',
  dailyPenaltyNotice: '71dba753-b23d-411c-8cac-e7b2bdd7418b',
  comments: 'Maxime saepe dolorem',
  externalContacts: [1],
  nonComplianceAttachments: {
    '71dba753-b23d-411c-8cac-e7b2bdd7418b': 'printPayment (19).pdf',
  },
  usersInfo: {
    'a3681dd5-bc93-4f35-9ed8-4d3dca8ad6d8': {
      name: 'Mock user name',
      roleCode: 'operator_admin',
      contactTypes: ['PRIMARY', 'SERVICE', 'FINANCIAL'],
    },
  },
};

export const mockState = {
  id: 14,
  type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED',
  payload: mockPayload,
  requestId: 'NC00001-1',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-09-02T15:13:05.611657Z',
} as unknown as CommonActionsState;

export const mockExternalContacts = {
  caExternalContacts: [
    {
      id: 1,
      name: 'Installation 5 Account',
      email: 'regulator5@trasys.gr',
      description: 'des',
      lastUpdatedDate: '2021-03-03T10:40:03.535662Z',
    },
    {
      id: 2,
      name: 'Legal5',
      email: 'qwe@qwreewrwe',
      description: 'dws',
      lastUpdatedDate: '2021-03-03T10:44:44.71144Z',
    },
  ],
  isEditable: true,
} as CaExternalContactsDTO;
