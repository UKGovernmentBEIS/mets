import {
  WasteQDRApplicationSubmittedRequestActionPayload,
  WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
  WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload,
} from 'pmrv-api';

export const wasteQdrSubmittedRequestActionPayload: WasteQDRApplicationSubmittedRequestActionPayload = {
  qdr: {
    reportProvided: true,
    reasonForUnprovided: '',
    report: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413',
    notes: 'Some notes',
    supportingFiles: ['bcc7f0bc-d7ec-4d11-8170-2a6451b71413'],
  },
  installationOperatorDetails: {
    installationName: 'Installation Name',
    siteName: 'Site Name',
    operator: 'Operator Name',
    operatorType: 'LIMITED_COMPANY',
    companyReferenceNumber: '12345678',
    emitterId: 'EM12345',
  },
  wasteQDRAttachments: { testFile: 'bcc7f0bc-d7ec-4d11-8170-2a6451b71413' },
};

export const wasteQdrReturnedForAmendsRequestActionPayload: WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload =
  {
    reviewDecision: {
      type: 'OPERATOR_AMENDS_NEEDED',
      details: {
        requiredChanges: [
          { reason: 'Reason 1', files: ['65092804-17c9-41a8-9ee0-4e728046bb3d'] },
          { reason: 'Reason 2' },
        ],
      } as WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
    },
  };
