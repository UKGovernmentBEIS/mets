import { AerCorsiaAnnualOffsettingPayload } from '@aviation/request-task/store';

import { AviationAerCorsiaAnnualOffsetting, RequestTaskItemDTO } from 'pmrv-api';

export const ANNUAL_OFFSETTING_REQUEST_TASK_ITEM: RequestTaskItemDTO = {
  requestInfo: {
    id: 'AEM-AO-00001-1',
    accountId: 1,
    type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING',
  },
  requestTask: {
    id: 2,
    type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD',
      aviationAerCorsiaAnnualOffsetting: {},
      aviationAerCorsiaAnnualOffsettingSectionsCompleted: {},
    } as any,
  },
};

export const AER_CORSIA_ANNUAL_OFFSETTING: AviationAerCorsiaAnnualOffsetting = {
  calculatedAnnualOffsetting: 992,
  schemeYear: 2023,
  sectorGrowth: 2.98,
  totalChapter: 333,
};

export const annualOffsettingMockBuild = (payload: AerCorsiaAnnualOffsettingPayload) => {
  return {
    requestTaskItem: {
      ...ANNUAL_OFFSETTING_REQUEST_TASK_ITEM,
      requestTask: {
        ...ANNUAL_OFFSETTING_REQUEST_TASK_ITEM.requestTask,
        payload,
      },
    },
    relatedTasks: [],
    timeline: [],
    isTaskReassigned: false,
    taskReassignedTo: '',
    tasksState: {},
    isEditable: true,
  };
};
