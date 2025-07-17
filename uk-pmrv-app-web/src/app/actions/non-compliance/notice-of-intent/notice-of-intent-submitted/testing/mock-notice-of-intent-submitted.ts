import { CommonActionsState } from '@actions/store/common-actions.state';

import { CaExternalContactsDTO, NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload } from 'pmrv-api';

export const mockPayload: NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD',
  noticeOfIntent: 'bfe44295-7668-4176-ba75-a6d2b3b0dc40',
  externalContacts: [1],
  comments: 'Distinctio Expedita',
  nonComplianceAttachments: {
    'bfe44295-7668-4176-ba75-a6d2b3b0dc40': 'printPayment (19).pdf',
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
  id: 15,
  type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED',
  payload: mockPayload,
  requestId: 'NC00001-1',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-09-02T15:14:18.887171Z',
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
