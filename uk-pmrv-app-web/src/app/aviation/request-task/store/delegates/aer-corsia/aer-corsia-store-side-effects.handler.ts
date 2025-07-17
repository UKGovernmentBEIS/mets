import { aerCorsiaEmissionsReductionClaimSideEffects } from '@aviation/request-task/aer/corsia/tasks/emissions-reduction-claim/store/emissions-reduction-claim-side-effects';
import { aerCorsiaAggregatedConsumptionFlightDataClaimSideEffects } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/store/aggregated-consumption-flight-data-side-effects';
import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
import { reportingObligationSideEffects } from '@aviation/request-task/aer/shared/reporting-obligation/store/reporting-obligation-side-effects';

import { AviationAerCorsiaAggregatedEmissionsData, AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AerRequestTaskPayload, AerTask, AerTaskKey } from '../../request-task.types';
import { RequestTaskSideEffectsHandler } from '../../requst-task-side-effects.handler';

export class AerCorsiaStoreSideEffectsHandler extends RequestTaskSideEffectsHandler<AerTaskKey, AerTask> {
  private reportingObligationHandler = reportingObligationSideEffects;
  private aerCorsiaEmissionsReductionHandler = aerCorsiaEmissionsReductionClaimSideEffects;
  private aerCorsiaAggregatedFlightDataHandler = aerCorsiaAggregatedConsumptionFlightDataClaimSideEffects;

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

    if ('emissionsReductionClaim' in update) {
      updatedPayload = this.aerCorsiaEmissionsReductionHandler(
        updatedPayload,
        update.emissionsReductionClaim as AviationAerCorsiaEmissionsReductionClaim,
      );
    }

    if ('aggregatedEmissionsData' in update) {
      updatedPayload = this.aerCorsiaAggregatedFlightDataHandler(
        updatedPayload,
        update.aggregatedEmissionsData as AviationAerCorsiaAggregatedEmissionsData,
      );
    }

    return updatedPayload;
  }

  private get payload(): AerRequestTaskPayload {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerRequestTaskPayload;
  }
}
