import {
  BDRApplicationSubmittedRequestActionPayload,
  BDRApplicationVerificationSubmittedRequestActionPayload,
  InstallationOperatorDetails,
} from 'pmrv-api';

const mockInstallationOperatorDetails = {
  companyReferenceNumber: '88888',
  installationLocation: {
    type: 'ONSHORE',
    address: {
      line1: 'Korinthou 4, Neo Psychiko',
      line2: 'line 2 legal test',
      city: 'Athens',
      country: 'GR',
      postcode: '15452',
    },
    gridReference: 'NN166712',
  },
  installationName: 'onshore permit installation',
  operator: 'onshore permit',
  operatorDetailsAddress: {
    line1: 'Korinthou 3, Neo Psychiko',
    line2: 'line 2 legal test',
    city: 'Athens',
    country: 'GR',
    postcode: '15451',
  },
  operatorType: 'LIMITED_COMPANY',
  siteName: 'site name',
} as InstallationOperatorDetails;

export const bdrSubmittedRequestActionPayload: BDRApplicationSubmittedRequestActionPayload = {
  bdr: {
    hasMmp: false,
    infoIsCorrectChecked: true,
    isApplicationForFreeAllocation: false,
    statusApplicationType: 'HSE',
    files: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
    bdrFile: '',
  },
  installationOperatorDetails: mockInstallationOperatorDetails,
  verificationPerformed: false,
  bdrAttachments: { testFile: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413' },
};

export const bdrVerificationSubmittedRequestActionPayload: BDRApplicationVerificationSubmittedRequestActionPayload = {
  bdr: {
    hasMmp: false,
    infoIsCorrectChecked: true,
    isApplicationForFreeAllocation: false,
    statusApplicationType: 'HSE',
    files: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
    bdrFile: '',
  },
  installationOperatorDetails: mockInstallationOperatorDetails,
  verificationPerformed: false,
  verificationReport: {
    opinionStatement: {
      notes: '123',
      opinionStatementFiles: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
    },
    overallAssessment: {
      type: 'VERIFIED_AS_SATISFACTORY',
    },
  },
  verificationAttachments: { testFile: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413' },
  bdrAttachments: { testFile: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413' },
};
