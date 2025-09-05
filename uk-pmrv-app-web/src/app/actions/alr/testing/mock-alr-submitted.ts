import {
  ALRApplicationAcceptedRequestActionPayload,
  ALRApplicationAcceptedWithCorrectionsRequestActionPayload,
  ALRApplicationProceededToAuthorityRequestActionPayload,
  ALRApplicationRejectedRequestActionPayload,
  ALRApplicationSubmittedRequestActionPayload,
  InstallationOperatorDetails,
} from 'pmrv-api';

const mockInstallationOperatorDetails = {
  installationName: 'installation oper 7',
  siteName: 'bxcvbxcb',
  installationLocation: {
    type: 'ONSHORE',
    gridReference: 'NN166712',
    address: {
      line1: 'Installation line 1',
      city: 'Installation city',
      country: 'Installation country',
      postcode: 'Installation postcode',
    },
  },
  operator: 'installation oper',
  operatorType: 'LIMITED_COMPANY',
  operatorDetailsAddress: {
    line1: 'Operator line 1',
    city: 'Operator city',
    country: 'Operator country',
    postcode: 'Operator postcode',
  },
  emitterId: 'EM00124',
} as InstallationOperatorDetails;

export const alrSubmittedRequestActionPayload:
  | ALRApplicationSubmittedRequestActionPayload
  | ALRApplicationProceededToAuthorityRequestActionPayload = {
  payloadType: 'ALR_APPLICATION_SUBMITTED_PAYLOAD',
  alr: {
    alrFile: '831f563a-ae02-4f83-a832-8fc840b56c7d',
    files: ['021049fb-c4dd-4a2b-85d3-e23609faab77', 'a1d94725-903d-493c-90fa-c80736b29c95'],
  },
  installationOperatorDetails: mockInstallationOperatorDetails,
  verificationPerformed: false,
  alrAttachments: {
    '021049fb-c4dd-4a2b-85d3-e23609faab77': 'test 1.txt',
    '831f563a-ae02-4f83-a832-8fc840b56c7d': 'test 2.txt',
    'a1d94725-903d-493c-90fa-c80736b29c95': 'test 3.txt',
  },
  verificationAttachments: {},
};

export const alrCompletedRequestActionPayload:
  | ALRApplicationAcceptedRequestActionPayload
  | ALRApplicationAcceptedWithCorrectionsRequestActionPayload
  | ALRApplicationRejectedRequestActionPayload = {
  payloadType: 'ALR_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD',
  authorityReviewOutcome: {
    alr: {
      alrFile: '3c4972b3-bdd6-4337-adef-d81136a27a39',
      files: ['35079a58-4fc5-4131-af05-fe358e735c01', 'aa44a806-db34-422f-89e8-86cd50d3d553'],
    },
    submissionDate: '2025-05-12',
    authorityResponse: {
      type: 'VALID_WITH_CORRECTIONS',
      authorityRespondDate: '2023-10-11',
      preliminaryAllocations: [
        {
          subInstallationName: 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS',
          year: 2034,
          allowances: 11,
          allocationId: '0',
        },
      ],
      totalAllocationsPerYear: {
        '2034': 77,
      },
      documents: ['a23ede42-c961-4363-a523-1f2d31e8ac35'],
      decisionNotice: 'gddgsf',
    },
  },
  decisionNotification: {
    signatory: 'ce447c34-19a7-4310-84c6-a2931f3ab9fd',
  },
  alrAttachments: {
    'a23ede42-c961-4363-a523-1f2d31e8ac35': 'sample_640×426.bmp',
  },
  usersInfo: {
    'ce447c34-19a7-4310-84c6-a2931f3ab9fd': {
      name: 'Regulator England',
    },
    'd099259e-539d-4b55-b375-813ab794980e': {
      name: 'instoper6 aaaaa',
      roleCode: 'operator_admin',
      contactTypes: ['FINANCIAL', 'SERVICE', 'PRIMARY'],
    },
  },
  officialNotice: {
    name: 'Activity_level_determination_approved_by_Authority_notice.pdf',
    uuid: '369ec981-eb4f-4583-9c3b-f01505cf080f',
  },
};
