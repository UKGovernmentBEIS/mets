import { TaskItemStatus } from '../shared/task-list/task-list.interface';
import { PermitTransferState } from './store/permit-transfer.state';

export function transferDetailsStatus(state: PermitTransferState): TaskItemStatus {
  if (!state.isRequestTask) {
    return 'complete';
  } else {
    const transferDetails = state?.permitTransferDetailsConfirmation;

    return (state.permitSectionsCompleted['transferDetails'] || [false])[0]
      ? 'complete'
      : transferDetails && Object.keys(transferDetails).length
        ? 'in progress'
        : 'not started';
  }
}
