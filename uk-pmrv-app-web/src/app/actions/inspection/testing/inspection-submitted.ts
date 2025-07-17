import { CommonActionsState } from '@actions/store/common-actions.state';

export const mockStateRegulator = {
  storeInitialized: true,
  action: {
    id: 1,
    requestAccountId: 13,
    submitter: 'Regulator',
    creationDate: '2022-11-29T12:12:48.469862Z',
    type: 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED',
    payload: {
      payloadType: 'INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD',
      installationInspection: {
        details: {
          date: '2023-11-11',
          officerNames: ['regulator england'],
          files: ['046195aa-1f32-484e-a01a-0f42ee8c353d'],
          additionalInformation: '123',
        },
        followUpActionsRequired: true,
        followUpActionsOmissionFiles: [],
        followUpActions: [
          {
            followUpActionType: 'NON_CONFORMITY',
            explanation: '111',
            followUpActionAttachments: ['f5a06e74-6771-43dd-a033-eae5e199bd31'],
          },
        ],
        responseDeadline: '2025-05-11',
      },
      sectionCompleted: false,
      inspectionAttachments: {
        '046195aa-1f32-484e-a01a-0f42ee8c353d': 'decision.PNG',
        '45bf2488-3466-4385-b17b-f1039a56005b': 'baseline.PNG',
        '6044c5b0-e438-418a-91b5-013279a9454f': 'completed.PNG',
        '62f75d9c-b836-4c94-b839-2aa8f6683fa4': 'opStatement.PNG',
        'a214be3f-35dd-4e6f-bada-796286c33c0d': 'outcome.PNG',
        'dc2347ba-5116-4003-bf88-7ca793e416eb': 'outcome.PNG',
        'f5a06e74-6771-43dd-a033-eae5e199bd31': 'baseline.PNG',
      },
      decisionNotification: {
        signatory: 'f9f19a63-7a5e-47ad-8a7a-8ec4b29fe70a',
      },
      usersInfo: {
        '3f37f956-de12-4a48-b21a-6c38aab95f15': {
          name: 'operator17 englandnew',
          roleCode: 'operator_admin',
          contactTypes: ['SERVICE', 'PRIMARY', 'FINANCIAL'],
        },
        'f9f19a63-7a5e-47ad-8a7a-8ec4b29fe70a': {
          name: 'regulator england',
        },
      },
      officialNotice: {
        name: 'INSTALLATION_ONSITE_INSPECTION_notice.pdf',
        uuid: '56e33c40-bf17-4ff2-99b0-283a118906c6',
      },
    },
  },
} as CommonActionsState;
