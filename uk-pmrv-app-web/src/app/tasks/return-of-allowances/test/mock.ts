import { RequestTaskItemDTO, RequestTaskPayload, ReturnOfAllowances, ReturnOfAllowancesReturned } from 'pmrv-api';

export const returnOfAllowancesCompleted: ReturnOfAllowances = {
  numberOfAllowancesToBeReturned: 20,
  years: [2021],
  reason: 'some reason',
  dateToBeReturned: '2050-05-05',
};

export const mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'RETURN_OF_ALLOWANCES_SAVE_APPLICATION',
    'RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION',
    'RETURN_OF_ALLOWANCES_CANCEL_APPLICATION',
  ],
  requestTask: {
    id: 1,
    type: 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD',
      returnOfAllowances: returnOfAllowancesCompleted,
      sectionsCompleted: {
        PROVIDE_DETAILS: true,
      },
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    id: 'RAE00022-2022-1',
    requestMetadata: {
      type: 'RETURN_OF_ALLOWANCES',
      year: '2022',
    } as any,
    type: 'RETURN_OF_ALLOWANCES',
    competentAuthority: 'ENGLAND',
    accountId: 22,
  },
};

export function updateMockedReturnOfAllowances(
  returnOfAllowancesPart?: Partial<ReturnOfAllowances>,
  sectionsCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
    requestTask: {
      ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask.payload,
        returnOfAllowances: {
          ...(mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask.payload as any)
            .returnOfAllowances,
          ...returnOfAllowancesPart,
        },
        sectionsCompleted: {
          PROVIDE_DETAILS:
            sectionsCompleted !== undefined
              ? sectionsCompleted
              : (mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask.payload as any)
                  .sectionsCompleted['PROVIDE_DETAILS'],
        },
      } as RequestTaskPayload,
    },
  };
}

export const returnOfAllowancesReturnedCompleted: ReturnOfAllowancesReturned = {
  isAllowancesReturned: false,
};

export const mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION'],
  requestTask: {
    id: 1,
    type: 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT_PAYLOAD',
      returnOfAllowancesReturned: returnOfAllowancesReturnedCompleted,
      sectionsCompleted: {
        PROVIDE_RETURNED_DETAILS: true,
      },
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    id: 'RAE00022-2022-1',
    requestMetadata: {
      type: 'RETURN_OF_ALLOWANCES',
      year: '2022',
    } as any,
    type: 'RETURN_OF_ALLOWANCES',
    competentAuthority: 'ENGLAND',
    accountId: 22,
  },
};

export function updateMockedReturnOfAllowancesReturned(
  returnOfAllowancesReturnedPart?: Partial<ReturnOfAllowancesReturned>,
  sectionsCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem,
    requestTask: {
      ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask.payload,
        returnOfAllowancesReturned: {
          ...(mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask.payload as any)
            .returnOfAllowancesReturned,
          ...returnOfAllowancesReturnedPart,
        },
        sectionsCompleted: {
          PROVIDE_RETURNED_DETAILS:
            sectionsCompleted !== undefined
              ? sectionsCompleted
              : (mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask.payload as any)
                  .sectionsCompleted['PROVIDE_RETURNED_DETAILS'],
        },
      } as RequestTaskPayload,
    },
  };
}
