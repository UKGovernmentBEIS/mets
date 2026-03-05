import { BDRS2ApplicationSubmittedRequestActionPayload, InstallationOperatorDetails } from 'pmrv-api';

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

export const bdrs2SubmittedRequestActionPayload: BDRS2ApplicationSubmittedRequestActionPayload = {
  bdrs2: {
    bdrs2guardQuestions: {
      continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
      covidAdjustments: true,
      inEiteSector: true,
      requiresAdditionalSubInstallationSplitsForCbam: true,
    },
    bdrs2Files: {
      supportingFiles: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
      file: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413',
    },
    mmpFiles: {
      supportingFiles: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
      file: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413',
    },
  },
  installationOperatorDetails: mockInstallationOperatorDetails,
  verificationPerformed: false,
  bdrs2Attachments: { testFile: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413' },
};
