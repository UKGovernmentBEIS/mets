import { reviewRequestTaskTypes } from '../shared/utils/permit';
import { PermitApplicationState } from '../store/permit-application.state';

export function deleteReturnUrl(
  state: PermitApplicationState,
  reviewGroupUrl: 'calculation' | 'fall-back' | 'measurement' | 'nitrous-oxide' | 'pfc',
): string {
  return reviewRequestTaskTypes.includes(state.requestTaskType) ? `../review/${reviewGroupUrl}/../../..` : '../../..';
}
