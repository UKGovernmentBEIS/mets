import { AerRequestTaskPayload, AerTask } from '@aviation/request-task/store/request-task.types';

export type AerSideEffectFn<T extends AerTask> = (payload: AerRequestTaskPayload, update: T) => AerRequestTaskPayload;
