import { InstallationOperatorDetails, NERApplicationSubmittedRequestActionPayload } from 'pmrv-api';

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

export const nerSubmittedRequestActionPayload: NERApplicationSubmittedRequestActionPayload = {
  payloadType: 'NER_APPLICATION_SUBMITTED_PAYLOAD',
  ner: {
    nerFiles: {
      file: '831f563a-ae02-4f83-a832-8fc840b56c7d',
      supportingFiles: ['021049fb-c4dd-4a2b-85d3-e23609faab77', 'a1d94725-903d-493c-90fa-c80736b29c95'],
    },
    mmpFiles: {
      file: '831f563a-ae02-4f83-a832-999999999999',
      supportingFiles: ['021049fb-c4dd-4a2b-85d3-999999999999', 'a1d94725-903d-493c-90fa-999999999999'],
    },
    notes: 'A note',
  },
  installationOperatorDetails: mockInstallationOperatorDetails,
  verificationPerformed: false,
  nerAttachments: {
    '021049fb-c4dd-4a2b-85d3-e23609faab77': 'test 1.txt',
    '831f563a-ae02-4f83-a832-8fc840b56c7d': 'test 2.txt',
    'a1d94725-903d-493c-90fa-c80736b29c95': 'test 3.txt',
    '021049fb-c4dd-4a2b-85d3-999999999999': 'test 4.txt',
    '831f563a-ae02-4f83-a832-999999999999': 'test 5.txt',
    'a1d94725-903d-493c-90fa-999999999999': 'test 6.txt',
  },
};
