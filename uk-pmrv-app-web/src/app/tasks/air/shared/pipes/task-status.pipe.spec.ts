import { mockAirApplicationSubmitPayload } from '@tasks/air/submit/testing/mock-air-application-submit-payload';

import { AirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  const pipe = new TaskStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform text based on their reference', () => {
    const mockPayload: AirApplicationSubmitRequestTaskPayload = {
      ...mockAirApplicationSubmitPayload,
      airSectionsCompleted: { '1': true, '2': false },
    };

    let transformation = pipe.transform(mockPayload, '1');
    expect(transformation).toEqual('complete');

    transformation = pipe.transform(mockPayload, '2');
    expect(transformation).toEqual('in progress');

    transformation = pipe.transform(mockPayload, '3');
    expect(transformation).toEqual('not started');
  });
});
