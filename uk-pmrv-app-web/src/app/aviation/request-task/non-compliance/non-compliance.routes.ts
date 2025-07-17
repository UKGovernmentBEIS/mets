import { inject } from '@angular/core';
import { CanActivateFn, Routes } from '@angular/router';

import { NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE } from '@tasks/non-compliance/non-compliance.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskStore } from '../store/request-task.store';

export const setRequestTaskStore: CanActivateFn = () => {
  const requestStore = inject(RequestTaskStore);
  const commonStore = inject(CommonTasksStore);

  requestStore.setState({
    requestTaskItem: commonStore.getState().requestTaskItem,
    relatedTasks: commonStore.getState().relatedTasks,
    timeline: commonStore.getState().timeLineActions,
    isTaskReassigned: false,
    taskReassignedTo: null,
    isEditable: commonStore.getState().isEditable,
    tasksState: null,
  });
  return true;
};

export const NON_COMPLIANCE_ROUTES: Routes = [
  {
    path: '',
    canDeactivate: [setRequestTaskStore],
    providers: [{ provide: NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE, useValue: false }],
    loadChildren: () =>
      import('./../../../tasks/non-compliance/non-compliance.module').then((m) => m.NonComplianceModule),
  },
];
