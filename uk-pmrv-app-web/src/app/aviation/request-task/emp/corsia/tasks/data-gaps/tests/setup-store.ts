import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '@aviation/request-task/store';

export function setupStore(store: RequestTaskStore, payload: EmpRequestTaskPayloadCorsia) {
  const state = store.getState();
  store.setState({
    ...state,
    isEditable: true,
    requestTaskItem: {
      ...state.requestTaskItem,
      requestTask: {
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
        payload,
      },
    },
  });
}
