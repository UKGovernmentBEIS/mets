import { VirTaskStatusPipe } from '@aviation/shared/pipes/vir-task-status.pipe';

import {
  AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AviationVirApplicationReviewRequestTaskPayload,
  AviationVirApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

describe('VirTaskStatusPipe', () => {
  const pipe = new VirTaskStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly show the status of reference', () => {
    const mockPayload: AviationVirApplicationSubmitRequestTaskPayload = {
      payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
      virSectionsCompleted: { E1: true, B1: false },
      verificationData: {
        priorYearIssues: {
          E1: {
            reference: 'E1',
            explanation: 'Test non-conformity from the previous year',
          },
          E2: {
            reference: 'E2',
            explanation: 'Test non-conformity from the previous year',
          },
        },
        uncorrectedNonConformities: {
          B1: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
        },
      },
      operatorImprovementResponses: {
        E1: {
          isAddressed: false,
          addressedDescription: 'Test description B1, when no',
          uploadEvidence: false,
          files: [],
        },
        B1: {
          isAddressed: false,
          addressedDescription: 'Test description B1, when no',
          uploadEvidence: false,
          files: [],
        },
      },
    };

    let transformation = pipe.transform(mockPayload, 'E1');
    expect(transformation).toEqual('complete');

    transformation = pipe.transform(mockPayload, 'B1');
    expect(transformation).toEqual('in progress');

    transformation = pipe.transform(mockPayload, 'E2');
    expect(transformation).toEqual('not started');

    transformation = pipe.transform(mockPayload, 'sendReport');
    expect(transformation).toEqual('cannot start yet');

    const mockPayloadCompleted: AviationVirApplicationSubmitRequestTaskPayload = {
      payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
      virSectionsCompleted: { E1: true, E2: true },
      verificationData: {
        priorYearIssues: {
          E1: {
            reference: 'E1',
            explanation: 'Test non-conformity from the previous year',
          },
          E2: {
            reference: 'E2',
            explanation: 'Test non-conformity from the previous year',
          },
        },
      },
      operatorImprovementResponses: {
        E1: {
          isAddressed: false,
          addressedDescription: 'Test description B1, when no',
          uploadEvidence: false,
          files: [],
        },
        E2: {
          isAddressed: false,
          addressedDescription: 'Test description B1, when no',
          uploadEvidence: false,
          files: [],
        },
      },
    };

    transformation = pipe.transform(mockPayloadCompleted, 'sendReport');
    expect(transformation).toEqual('not started');

    const mockReviewPayload: AviationVirApplicationReviewRequestTaskPayload = {
      payloadType: 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD',
      reviewSectionsCompleted: { E1: true, B1: false, createSummary: false },
      verificationData: {
        priorYearIssues: {
          E1: {
            reference: 'E1',
            explanation: 'Test non-conformity from the previous year',
          },
          E2: {
            reference: 'E2',
            explanation: 'Test non-conformity from the previous year',
          },
        },
        uncorrectedNonConformities: {
          B1: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
        },
      },
      regulatorReviewResponse: {
        regulatorImprovementResponses: {
          E1: {
            improvementRequired: false,
            improvementDeadline: null,
            improvementComments: 'Test description B1, changed',
            operatorActions: 'Test operator actions B1',
          },
          B1: {
            improvementRequired: false,
            improvementDeadline: null,
            improvementComments: 'Test description B1, changed',
            operatorActions: 'Test operator actions B1',
          },
        },
        reportSummary: 'Test summary',
      },
    };

    transformation = pipe.transform(mockReviewPayload, 'E1');
    expect(transformation).toEqual('complete');

    transformation = pipe.transform(mockReviewPayload, 'B1');
    expect(transformation).toEqual('in progress');

    transformation = pipe.transform(mockReviewPayload, 'E2');
    expect(transformation).toEqual('not started');

    transformation = pipe.transform(mockReviewPayload, 'createSummary');
    expect(transformation).toEqual('in progress');

    transformation = pipe.transform(mockReviewPayload, 'sendReport');
    expect(transformation).toEqual('cannot start yet');

    const mockReviewPayloadCompleted: AviationVirApplicationReviewRequestTaskPayload = {
      payloadType: 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD',
      reviewSectionsCompleted: { E1: true, E2: true, B1: true, createSummary: true },
      verificationData: {
        priorYearIssues: {
          E1: {
            reference: 'E1',
            explanation: 'Test non-conformity from the previous year',
          },
          E2: {
            reference: 'E2',
            explanation: 'Test non-conformity from the previous year',
          },
        },
        uncorrectedNonConformities: {
          B1: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
        },
      },
      regulatorReviewResponse: {
        regulatorImprovementResponses: {
          E1: {
            improvementRequired: false,
            improvementDeadline: null,
            improvementComments: 'Test description B1, changed',
            operatorActions: 'Test operator actions B1',
          },
          E2: {
            improvementRequired: false,
            improvementDeadline: null,
            improvementComments: 'Test description B1, changed',
            operatorActions: 'Test operator actions B1',
          },
          B1: {
            improvementRequired: false,
            improvementDeadline: null,
            improvementComments: 'Test description B1, changed',
            operatorActions: 'Test operator actions B1',
          },
        },
        reportSummary: 'Test summary',
      },
    };

    transformation = pipe.transform(mockReviewPayloadCompleted, 'sendReport');
    expect(transformation).toEqual('not started');

    const mockRespondPayload: AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload = {
      payloadType: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
      virRespondToRegulatorCommentsSectionsCompleted: { E1: true, B1: false },
      verificationData: {
        priorYearIssues: {
          E1: {
            reference: 'E1',
            explanation: 'Test non-conformity from the previous year',
          },
          E2: {
            reference: 'E2',
            explanation: 'Test non-conformity from the previous year',
          },
        },
        uncorrectedNonConformities: {
          B1: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
        },
      },
      operatorImprovementFollowUpResponses: {
        E1: {
          improvementCompleted: false,
          reason: 'E1 reason',
        },
        B1: {
          improvementCompleted: false,
          reason: 'E1 reason',
        },
      },
    };

    transformation = pipe.transform(mockRespondPayload, 'E1', false);
    expect(transformation).toEqual('complete');
    transformation = pipe.transform(mockRespondPayload, 'E1', true);
    expect(transformation).toEqual('not started');

    transformation = pipe.transform(mockRespondPayload, 'B1', false);
    expect(transformation).toEqual('in progress');
    transformation = pipe.transform(mockRespondPayload, 'B1', true);
    expect(transformation).toEqual('cannot start yet');

    transformation = pipe.transform(mockRespondPayload, 'E2', false);
    expect(transformation).toEqual('not started');
    transformation = pipe.transform(mockRespondPayload, 'E2', true);
    expect(transformation).toEqual('cannot start yet');
  });
});
