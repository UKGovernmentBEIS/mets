import { aggregateEmissionSourcesSideEffects } from '@aviation/request-task/emp/ukets/tasks/emission-sources/store/emission-sources-side-effects';

import { EmpEmissionsMonitoringApproach, EmpEmissionSources } from 'pmrv-api';

import { aggregateMonitoringApproachSideEffects } from '../../../emp/ukets/tasks/monitoring-approach/store/monitoring-approach-side-effects';
import { RequestTaskStore } from '../../request-task.store';
import { EmpRequestTaskPayloadUkEts, EmpTask, EmpTaskKey } from '../../request-task.types';
import { RequestTaskSideEffectsHandler } from '../../requst-task-side-effects.handler';

export type EmpSideEffectFn<T extends EmpTask> = (
  payload: EmpRequestTaskPayloadUkEts,
  update: T,
) => EmpRequestTaskPayloadUkEts;

export class EmpUkEtsStoreSideEffectsHandler extends RequestTaskSideEffectsHandler<EmpTaskKey, EmpTask> {
  private monitoringApproachHandler = aggregateMonitoringApproachSideEffects;
  private emissionSourcesHandler = aggregateEmissionSourcesSideEffects;

  constructor(store: RequestTaskStore) {
    super(store);
  }

  applySideEffects(update: { [key in EmpTaskKey]?: EmpTask }): EmpRequestTaskPayloadUkEts {
    let updatedPayload = this.payload;

    if ('emissionsMonitoringApproach' in update) {
      updatedPayload = this.monitoringApproachHandler(
        updatedPayload,
        update.emissionsMonitoringApproach as EmpEmissionsMonitoringApproach,
      );
    }

    if ('emissionSources' in update) {
      updatedPayload = this.emissionSourcesHandler(updatedPayload, update.emissionSources as EmpEmissionSources);
    }

    return updatedPayload;
  }

  private get payload(): EmpRequestTaskPayloadUkEts {
    return this.store.getState().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts;
  }
}
