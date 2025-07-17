import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestActionStore } from '@aviation/request-action/store';
import { OperatorDetailsCorsiaFormProvider } from '@aviation/request-task/emp/corsia/tasks/operator-details';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { SHARED_EMP_ROUTES } from '../shared/emp.routes';

const canDeactivateEmp: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

export const EMP_ROUTES: Routes = [
  {
    path: '',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider }],
    canDeactivate: [canDeactivateEmp],
    children: [
      {
        path: 'submitted',
        children: [
          ...SHARED_EMP_ROUTES,
          {
            path: 'operator-details',
            data: { breadcrumb: 'Operator details' },
            loadComponent: () => import('./tasks').then((c) => c.OperatorDetailsComponent),
          },
        ],
      },
    ],
  },
];
