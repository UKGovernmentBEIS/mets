import { CommonActionsState } from '@actions/store/common-actions.state';

import { CaExternalContactsDTO, NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload } from 'pmrv-api';

export const mockPayload: NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD',
  civilPenalty: '4c9efe3d-38ef-4379-ac65-caa215b49155',
  penaltyAmount: 'Qui et delectus nos',
  externalContacts: [1],
  dueDate: '2014-10-09',
  comments: 'Nulla repellendus V',
  nonComplianceAttachments: {
    '4c9efe3d-38ef-4379-ac65-caa215b49155': 'printPayment (19).pdf',
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
  id: 16,
  type: 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED',
  payload: mockPayload,
  requestId: 'NC00001-1',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-09-02T15:14:50.481665Z',
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
