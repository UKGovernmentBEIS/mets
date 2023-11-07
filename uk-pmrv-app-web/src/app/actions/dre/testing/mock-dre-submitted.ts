import moment from 'moment';

import { CommonActionsState } from '../../store/common-actions.state';

export const mockState = {
  storeInitialized: true,
  action: {
    type: 'DRE_APPLICATION_SUBMITTED',
    payload: {
      dre: {
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
      },
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
