import { RequestTaskPayload } from 'pmrv-api';

import { RequestTaskStore } from './request-task.store';

export abstract class RequestTaskSideEffectsHandler<TASK_KEY extends string, TASK> {
  protected constructor(protected store: RequestTaskStore) {}

  abstract applySideEffects(update: { [key in TASK_KEY]: TASK }): RequestTaskPayload;
}
