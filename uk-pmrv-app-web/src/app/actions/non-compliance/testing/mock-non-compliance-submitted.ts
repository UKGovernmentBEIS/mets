import { CommonActionsState } from '../../store/common-actions.state';

export const mockState = {
  storeInitialized: true,
  action: {
    type: 'NON_COMPLIANCE_APPLICATION_SUBMITTED',
    payload: {
      civilPenalty: true,
      comments: 'comments step 11',
      complianceDate: '2022-02-22',
      dailyPenalty: true,
      nonComplianceDate: '2024-11-11',
      reason: 'FAILURE_TO_SURRENDER_ALLOWANCES',
      selectedRequests: ['AEM00094'],
      availableRequests: [{ id: 'AEM00094', type: 'INSTALLATION_ACCOUNT_OPENING' }],
      nonComplianceAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      decisionNotification: {
        operators: ['975eb886-cc78-40f4-95ca-a98e665af6ca'],
        signatory: 'c5508398-b325-46ab-81ff-d83024aff5ce',
      },
      usersInfo: {
        'c5508398-b325-46ab-81ff-d83024aff5ce': {
          name: 'Regulator England',
        },
        '975eb886-cc78-40f4-95ca-a98e665af6ca': {
          name: 'John Doe',
          roleCode: 'operator_admin',
          contactTypes: ['PRIMARY', 'FINANCIAL', 'SERVICE'],
        },
      },
      officialNotice: {
        name: 'off notice.pdf',
        uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
      },
    },
  },
} as CommonActionsState;
