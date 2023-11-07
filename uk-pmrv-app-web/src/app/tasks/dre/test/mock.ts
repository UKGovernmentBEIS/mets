import moment from 'moment';

import { Dre, RequestTaskItemDTO, RequestTaskPayload } from 'pmrv-api';

export const dreCompleted: Dre = {
  determinationReason: {
    type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
    operatorAskedToResubmit: true,
    regulatorComments: 'dfdf',
    supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
  },
  officialNoticeReason: 'fdfdf',
  monitoringApproachReportingEmissions: {
    FALLBACK: {
      type: 'FALLBACK',
      emissions: {
        reportableEmissions: 1,
        sustainableBiomass: 2,
      },
    } as any,
  },
  informationSources: ['fgf'],
  fee: {
    feeDetails: {
      dueDate: moment().add(1, 'day').format('YYYY-MM-DD'),
      hourlyRate: '3',
      totalBillableHours: '34',
    },
    chargeOperator: true,
  },
};

export const mockCompletedDreApplicationSubmitRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['DRE_SUBMIT_NOTIFY_OPERATOR'],
  requestTask: {
    id: 1,
    type: 'DRE_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'DRE_APPLICATION_SUBMIT_PAYLOAD',
      dre: dreCompleted,
      dreAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      sectionCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    id: 'DRE00022-2022-1',
    requestMetadata: {
      type: 'DRE',
      year: '2022',
    } as any,
    type: 'DRE',
    competentAuthority: 'ENGLAND',
    accountId: 22,
  },
};

export function updateMockedDre(drePart?: Partial<Dre>, sectionCompleted?: boolean): RequestTaskItemDTO {
  return {
    ...mockCompletedDreApplicationSubmitRequestTaskItem,
    requestTask: {
      ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.payload,
        dre: {
          ...(mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.payload as any).dre,
          ...drePart,
        },
        sectionCompleted:
          sectionCompleted !== undefined
            ? sectionCompleted
            : (mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.payload as any).sectionCompleted,
      } as RequestTaskPayload,
    },
  };
}
