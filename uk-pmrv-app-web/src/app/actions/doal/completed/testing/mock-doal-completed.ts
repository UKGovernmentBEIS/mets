import {
  DoalApplicationAcceptedRequestActionPayload,
  DoalApplicationAcceptedWithCorrectionsRequestActionPayload,
  DoalApplicationRejectedRequestActionPayload,
  DoalGrantAuthorityWithCorrectionsResponse,
} from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockDoalApplicationCompletedRequestActionPayload:
  | DoalApplicationAcceptedRequestActionPayload
  | DoalApplicationAcceptedWithCorrectionsRequestActionPayload
  | DoalApplicationRejectedRequestActionPayload = {
  payloadType: 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD',
  doalAuthority: {
    dateSubmittedToAuthority: {
      date: '2023-03-12',
    },
    authorityResponse: {
      type: 'VALID_WITH_CORRECTIONS',
      authorityRespondDate: '2023-03-12',
      decisionNotice: 'Decision notice',
      preliminaryAllocations: [
        {
          year: 2024,
          allowances: 200,
          subInstallationName: 'ALUMINIUM',
        },
        {
          year: 2023,
          allowances: 100,
          subInstallationName: 'ALUMINIUM',
        },
      ],
    } as DoalGrantAuthorityWithCorrectionsResponse,
  },
  doalAttachments: {},
  officialNotice: {
    name: 'Activity_level_determination_preliminary_allocation_letter.pdf',
    uuid: 'fb355af4-2163-443e-b9ea-7c483bc217f9',
  },
  usersInfo: {
    '252cd19d-b2f4-4e6b-ba32-a225d0777c98': {
      name: 'Regulator1 England',
    },
    '43edf1df-a814-4ce5-8839-e6b2c80cd63e': {
      name: 'Operator2 England',
      roleCode: 'operator_admin',
      contactTypes: ['SECONDARY'],
    },
    '0f15e721-7c71-4441-b818-5cb2bf2f162b': {
      name: 'Operator1 England',
      roleCode: 'operator_admin',
      contactTypes: ['FINANCIAL', 'PRIMARY', 'SERVICE'],
    },
  },
  decisionNotification: {
    operators: ['43edf1df-a814-4ce5-8839-e6b2c80cd63e'],
    externalContacts: [1],
    signatory: '252cd19d-b2f4-4e6b-ba32-a225d0777c98',
  },
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS',
    payload: mockDoalApplicationCompletedRequestActionPayload,
    requestId: 'DOAL00011-2021-1',
    requestType: 'DOAL',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Regulator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
