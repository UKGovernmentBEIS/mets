import { verifierDetailsSideEffects } from '@aviation/request-task/aer/ukets/aer-verify/tasks/verifier-details/store/verifier-details-side-effects';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AerVerifyTask, AerVerifyTaskKey, AerVerifyTaskPayload } from '../../request-task.types';
import { RequestTaskSideEffectsHandler } from '../../requst-task-side-effects.handler';

export type AerVerifySideEffectFn<T extends AerVerifyTask> = (
  payload: AerVerifyTaskPayload,
  update: T,
) => AerVerifyTaskPayload;

export class AerVerifyUkEtsStoreSideEffectsHandler extends RequestTaskSideEffectsHandler<
  AerVerifyTaskKey,
  AerVerifyTask
> {
  private verifierDetailsHandler = verifierDetailsSideEffects;

  constructor(store: RequestTaskStore) {
    super(store);
  }

  applySideEffects(update: { [key in AerVerifyTaskKey]?: AerVerifyTask }): AerVerifyTaskPayload {
    let updatedPayload = this.payload;

    if ('verificationReport' in update) {
      updatedPayload = this.verifierDetailsHandler(
        updatedPayload,
        update.verificationReport as AviationAerUkEtsVerificationReport,
      );
    }

    return updatedPayload;
  }

  private get payload(): AerVerifyTaskPayload {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerVerifyTaskPayload;
  }
}
