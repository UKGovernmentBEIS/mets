import { RequestActionDTO } from 'pmrv-api';

export interface RequestActionState {
  requestActionItem: RequestActionDTO;
  regulatorViewer: boolean;
}

export const initialState: RequestActionState = {
  requestActionItem: null,
  regulatorViewer: false,
};
