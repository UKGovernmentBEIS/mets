import { mockAirApplicationRespondPayload } from '@tasks/air/comments-response/testing/mock-air-application-respond-payload';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

import { SubmitRespondStatusPipe } from './submit-respond-status.pipe';

describe('SubmitRespondStatusPipe', () => {
  const pipe = new SubmitRespondStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform text based on their reference', () => {
    const mockPayload: AirApplicationRespondToRegulatorCommentsRequestTaskPayload = {
      ...mockAirApplicationRespondPayload,
      airRespondToRegulatorCommentsSectionsCompleted: {
        A1: true,
      },
    };

    let transformation = pipe.transform(mockPayload, 'A1');
    expect(transformation).toEqual('not started');

    transformation = pipe.transform(mockPayload, 'B2');
    expect(transformation).toEqual('cannot start yet');
  });
});
