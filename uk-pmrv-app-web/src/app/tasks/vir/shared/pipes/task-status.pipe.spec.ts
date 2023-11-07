import { mockVirApplicationSubmitPayload } from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';

import { VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  const pipe = new TaskStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform text based on their reference', () => {
    const mockPayload: VirApplicationSubmitRequestTaskPayload = {
      ...mockVirApplicationSubmitPayload,
      virSectionsCompleted: { A1: true, B2: false },
    };

    let transformation = pipe.transform(mockPayload, 'A1');
    expect(transformation).toEqual('complete');

    transformation = pipe.transform(mockPayload, 'B2');
    expect(transformation).toEqual('in progress');

    transformation = pipe.transform(mockPayload, 'E2');
    expect(transformation).toEqual('not started');
  });
});
