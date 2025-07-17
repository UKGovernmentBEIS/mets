import { AerCorsia3YearOffsettingPayload } from '@aviation/request-task/store';

import { AviationAerCorsia3YearPeriodOffsetting, RequestTaskItemDTO } from 'pmrv-api';

export const THREE_YEAR_PERIOD_OFFSETTING_REQUEST_TASK_ITEM: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SAVE_APPLICATION',
    'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CANCEL_APPLICATION',
  ],
  requestInfo: {
    id: 'AEM-3YPO-00001-1',
    accountId: 1,
    type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING',
  },
  requestTask: {
    id: 2,
    type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD',
      aviationAerCorsia3YearPeriodOffsetting: { schemeYears: ['2021', '2022', '2023'] },
      aviationAerCorsia3YearPeriodOffsettingSectionsCompleted: {},
    } as unknown as AerCorsia3YearOffsettingPayload,
  },
};

export const INITIATE_OFFSETTING_REQUIREMENTS: AviationAerCorsia3YearPeriodOffsetting = {
  schemeYears: [2021, 2022, 2023],
  yearlyOffsettingData: {
    2021: {
      calculatedAnnualOffsetting: null,
      cefEmissionsReductions: null,
    },
    2022: {
      calculatedAnnualOffsetting: null,
      cefEmissionsReductions: null,
    },
    2023: {
      calculatedAnnualOffsetting: null,
      cefEmissionsReductions: null,
    },
  },
  totalYearlyOffsettingData: {
    calculatedAnnualOffsetting: null,
    cefEmissionsReductions: null,
  },
  periodOffsettingRequirements: null,
  operatorHaveOffsettingRequirements: null,
};

export const AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING: AviationAerCorsia3YearPeriodOffsetting = {
  schemeYears: [2021, 2022, 2023],
  yearlyOffsettingData: {
    2021: {
      calculatedAnnualOffsetting: 221,
      cefEmissionsReductions: 11,
    },
    2022: {
      calculatedAnnualOffsetting: 44,
      cefEmissionsReductions: 33,
    },
    2023: {
      calculatedAnnualOffsetting: 66,
      cefEmissionsReductions: 55,
    },
  },
  totalYearlyOffsettingData: {
    calculatedAnnualOffsetting: 331,
    cefEmissionsReductions: 99,
  },
  periodOffsettingRequirements: 232,
  operatorHaveOffsettingRequirements: true,
};

export const threeYearPeriodlOffsettingMockBuild = (payload: AerCorsia3YearOffsettingPayload) => {
  return {
    requestTaskItem: {
      ...THREE_YEAR_PERIOD_OFFSETTING_REQUEST_TASK_ITEM,
      requestTask: {
        ...THREE_YEAR_PERIOD_OFFSETTING_REQUEST_TASK_ITEM.requestTask,
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
