import { DoalApplicationProceededToAuthorityRequestActionPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockDoalApplicationProceededRequestActionPayload: DoalApplicationProceededToAuthorityRequestActionPayload =
  {
    payloadType: 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD',
    reportingYear: 2023,
    doal: {
      operatorActivityLevelReport: {
        document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
        areActivityLevelsEstimated: false,
        comment: 'Operator activity level report comment',
      },
      verificationReportOfTheActivityLevelReport: {
        document: 'abf68262-f6d1-4137-b654-c3302079d023',
        comment: 'Verification report of the activity level report comment',
      },
      additionalDocuments: {
        exist: true,
        documents: ['7e2036b4-c857-4caa-afef-97e690df3454'],
        comment: 'Additional documents comment',
      },
      activityLevelChangeInformation: {
        activityLevels: [
          {
            year: 2025,
            subInstallationName: 'ADIPIC_ACID',
            changeType: 'INCREASE',
            changedActivityLevel: 'Changed activity level',
            comments: 'Activity level 1 comment',
          },
        ],
        areConservativeEstimates: true,
        explainEstimates: 'Explain estimates',
        preliminaryAllocations: [
          {
            subInstallationName: 'ALUMINIUM',
            year: 2025,
            allowances: 10,
          },
        ],
        commentsForUkEtsAuthority: 'Comments for UkEts authority comment',
      },
      determination: {
        type: 'PROCEED_TO_AUTHORITY',
        reason: 'Reason',
        articleReasonGroupType: 'ARTICLE_6A_REASONS',
        articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
        hasWithholdingOfAllowances: true,
        withholdingAllowancesNotice: {
          noticeIssuedDate: '2022-08-10',
          withholdingOfAllowancesComment: 'Withholding of allowances comment',
        },
        needsOfficialNotice: true,
      } as DoalProceedToAuthorityDetermination,
    },
    doalAttachments: {
      '2b587c89-1973-42ba-9682-b3ea5453b9dd': '1.png',
      'abf68262-f6d1-4137-b654-c3302079d023': '2.png',
      '7e2036b4-c857-4caa-afef-97e690df3454': '3.png',
    },
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
    type: 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY',
    payload: mockDoalApplicationProceededRequestActionPayload,
    requestId: 'DOAL00011-2021-1',
    requestType: 'DOAL',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Regulator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
