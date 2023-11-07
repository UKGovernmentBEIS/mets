import { aggregateEmissionSourcesSideEffects } from '@aviation/request-task/emp/corsia/tasks/emission-sources/store/emission-sources-side-effects';
import { aggregateMonitoringApproachSideEffects } from '@aviation/request-task/emp/corsia/tasks/monitoring-approach/store/monitoring-approach-side-effects';

import { EmpEmissionsMonitoringApproachCorsia, EmpEmissionSourcesCorsia } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { EmpCorsiaTask, EmpCorsiaTaskKey, EmpRequestTaskPayloadCorsia } from '../../request-task.types';
import { RequestTaskSideEffectsHandler } from '../../requst-task-side-effects.handler';

export type EmpSideEffectFn<T extends EmpCorsiaTask> = (
  payload: EmpRequestTaskPayloadCorsia,
  update: T,
) => EmpRequestTaskPayloadCorsia;

export class EmpCorsiaStoreSideEffectsHandler extends RequestTaskSideEffectsHandler<EmpCorsiaTaskKey, EmpCorsiaTask> {
  private monitoringApproachHandler = aggregateMonitoringApproachSideEffects;
  private emissionSourcesHandler = aggregateEmissionSourcesSideEffects;

  constructor(store: RequestTaskStore) {
    super(store);
  }

  applySideEffects(update: { [key in EmpCorsiaTaskKey]?: EmpCorsiaTask }): EmpRequestTaskPayloadCorsia {
    let updatedPayload = this.payload;

    if ('emissionsMonitoringApproach' in update) {
      updatedPayload = this.monitoringApproachHandler(
        updatedPayload,
        update.emissionsMonitoringApproach as EmpEmissionsMonitoringApproachCorsia,
      );
    }

    if ('emissionSources' in update) {
      updatedPayload = this.emissionSourcesHandler(updatedPayload, update.emissionSources as EmpEmissionSourcesCorsia);
    }

    return updatedPayload;
  }

  private get payload(): EmpRequestTaskPayloadCorsia {
    return this.store.getState().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadCorsia;
  }
}
