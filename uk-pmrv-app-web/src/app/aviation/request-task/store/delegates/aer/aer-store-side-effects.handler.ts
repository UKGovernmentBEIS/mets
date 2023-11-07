import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
import { reportingObligationSideEffects } from '@aviation/request-task/aer/shared/reporting-obligation/store/reporting-obligation-side-effects';
import { aerDataGapsSideEffects } from '@aviation/request-task/aer/ukets/tasks/data-gaps/store/data-gaps-side-effects';
import { aerMonitoringApproachSideEffects } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach/store/monitoring-approach-side-effects';

import { AviationAerDataGaps, AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AerRequestTaskPayload, AerTask, AerTaskKey } from '../../request-task.types';
import { RequestTaskSideEffectsHandler } from '../../requst-task-side-effects.handler';

export type AerSideEffectFn<T extends AerTask> = (payload: AerRequestTaskPayload, update: T) => AerRequestTaskPayload;

export class AerStoreSideEffectsHandler extends RequestTaskSideEffectsHandler<AerTaskKey, AerTask> {
  private reportingObligationHandler = reportingObligationSideEffects;
  private aerMonitoringApproachHandler = aerMonitoringApproachSideEffects;
  private aerDataGapsHandler = aerDataGapsSideEffects;

  constructor(store: RequestTaskStore) {
    super(store);
  }

  applySideEffects(update: { [key in AerTaskKey]?: AerTask }): AerRequestTaskPayload {
    let updatedPayload = this.payload;

    if ('reportingObligation' in update) {
      updatedPayload = this.reportingObligationHandler(
        updatedPayload,
        update.reportingObligation as ReportingObligation,
      );
    }

    if ('monitoringApproach' in update) {
      updatedPayload = this.aerMonitoringApproachHandler(
        updatedPayload,
        update.monitoringApproach as AviationAerEmissionsMonitoringApproach,
      );
    }

    if ('aggregatedEmissionsData' in update) {
      updatedPayload = this.aerDataGapsHandler(updatedPayload, update.dataGaps as AviationAerDataGaps);
    }

    return updatedPayload;
  }

  private get payload(): AerRequestTaskPayload {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerRequestTaskPayload;
  }
}
