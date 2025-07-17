import { TaskItemStatus } from '../shared/task-list/task-list.interface';
import { PermitVariationState } from './store/permit-variation.state';

export function variationDetailsStatus(state: PermitVariationState): TaskItemStatus {
  if (!state.isRequestTask) {
    return 'complete';
  } else {
    const aboutVariation = state?.permitVariationDetails;

    return state.permitVariationDetailsCompleted
      ? 'complete'
      : aboutVariation && Object.keys(aboutVariation).length
        ? 'in progress'
        : 'not started';
  }
}
