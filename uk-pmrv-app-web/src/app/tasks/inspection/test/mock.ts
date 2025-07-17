import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  InstallationInspectionOperatorRespondRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { InspectionSubmitRequestTaskPayload } from '../shared/inspection';

export const mockOfficers = [
  {
    id: '45b2620b-c859-4296-bb58-e49f180f6137',
    firstName: 'newreg1',
    lastName: 'User',
  },
  {
    id: 'eaa82cc8-0a7d-4f2d-bcf7-f54f612f59e5',
    firstName: 'newreg2',
    lastName: 'User',
  },
  {
    id: '44c7a770-18b2-40e8-85ee-5c92210618d7',
    firstName: 'newreg3',
    lastName: 'User',
  },
];

export const mockInspectionSubmitRequestTaskPayload: InspectionSubmitRequestTaskPayload = {
  payloadType: 'INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD',
  installationInspection: {
    details: {
      files: [],
      officerNames: [
        mockOfficers[1].firstName + ' ' + mockOfficers[1].lastName,
        mockOfficers[2].firstName + ' ' + mockOfficers[2].lastName,
      ],
      regulatorExtraFiles: [],
      additionalInformation: 'additionalInformation 1',
    },
    followUpActionsRequired: true,
    followUpActions: [
      {
        explanation: 'Reiciendis nulla qua',
        followUpActionAttachments: ['c3344a0a-71cb-46e7-9be7-4be3264ac4d9'],
        followUpActionType: 'NON_CONFORMITY',
      },
      {
        explanation: 'Vitae facere est as',
        followUpActionAttachments: [],
        followUpActionType: 'NON_CONFORMITY',
      },
    ],
    responseDeadline: '2026-02-20T00:00:00.000Z',
  },
  installationInspectionSectionsCompleted: {
    details: true,
    followUpAction: true,
  },
  inspectionAttachments: {
    'c3344a0a-71cb-46e7-9be7-4be3264ac4d9': 'aircraftdata2 1.csv',
  },
};

export const mockInspectionRespondRequestTaskPayload: InstallationInspectionOperatorRespondRequestTaskPayload = {
  payloadType: 'INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD',
  installationInspection: mockInspectionSubmitRequestTaskPayload.installationInspection,
  followupActionsResponses: {
    0: {
      completed: true,
      explanation: 'Action response 1 explanation',
      completionDate: '2026-02-20T00:00:00.000Z',
    },
    1: {
      completed: true,
      explanation: 'Action response 2 explanation',
      completionDate: '2026-03-20T00:00:00.000Z',
    },
  },
  installationInspectionOperatorRespondSectionsCompleted: {
    0: true,
  },
  inspectionAttachments: mockInspectionSubmitRequestTaskPayload.inspectionAttachments,
};

const commonState = {
  requestInfo: {
    id: 'INS00107-2022-1',
    type: 'INSTALLATION_AUDIT',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'INSTALLATION_INSPECTION',
      year: '2022',
    },
  },
  requestTask: {
    id: 1,
    assignable: true,
    assigneeFullName: 'Regulator1 England',
    assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
    startDate: '2023-03-15T15:04:23.866188Z',
  } as RequestTaskDTO,
};

export const inspectionSubmitMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [
      'INSTALLATION_AUDIT_SAVE_APPLICATION',
      'INSTALLATION_AUDIT_CANCEL_APPLICATION',
      'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT',
      'INSTALLATION_AUDIT_REQUEST_PEER_REVIEW',
      'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR',
    ],
    requestTask: {
      ...commonState.requestTask,
      type: 'INSTALLATION_AUDIT_APPLICATION_SUBMIT',
      payload: mockInspectionSubmitRequestTaskPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export const inspectionRespondMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [
      'INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE',
      'INSTALLATION_AUDIT_OPERATOR_RESPOND_SUBMIT',
      'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT',
    ],
    requestTask: {
      ...commonState.requestTask,
      type: 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS',
      payload: mockInspectionRespondRequestTaskPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function inspectionMockStateBuild(value?: any): CommonTasksState {
  return {
    ...inspectionSubmitMockState,
    requestTaskItem: {
      ...inspectionSubmitMockState.requestTaskItem,
      requestTask: {
        ...inspectionSubmitMockState.requestTaskItem.requestTask,
        payload: {
          ...mockInspectionSubmitRequestTaskPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function inspectionForSubmitMockPostBuild(
  value?: any,
  installationInspectionSectionsCompleted?: InspectionSubmitRequestTaskPayload['installationInspectionSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'INSTALLATION_AUDIT_SAVE_APPLICATION',
    requestTaskId: inspectionSubmitMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'INSTALLATION_AUDIT_APPLICATION_SAVE_PAYLOAD',
      ...value,
      installationInspectionSectionsCompleted: {
        ...mockInspectionSubmitRequestTaskPayload.installationInspectionSectionsCompleted,
        ...installationInspectionSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export const inspectionForRespondMockPostBuild = (
  value?: any,
  sectionsCompleted?: InstallationInspectionOperatorRespondRequestTaskPayload['installationInspectionOperatorRespondSectionsCompleted'],
): RequestTaskActionProcessDTO => {
  return {
    requestTaskActionType: 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE',
    requestTaskId: inspectionRespondMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE_PAYLOAD',
      ...value,
      installationInspectionOperatorRespondSectionsCompleted: {
        ...mockInspectionRespondRequestTaskPayload.installationInspectionOperatorRespondSectionsCompleted,
        ...sectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
};
