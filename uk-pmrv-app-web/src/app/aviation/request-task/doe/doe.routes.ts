import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { RequestTaskStore } from '../store';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import {
  canActivateDoeAviationEmissions,
  canDeactivateDoeAviationEmissions,
} from './tasks/aviation-emissions/doe-aviation-emissions.guards';
import { DoeCorsiaEmissionsFormProvider } from './tasks/aviation-emissions/doe-corsia-emissions-form.provider';
import { DoeNotifyOperatorComponent } from './tasks/notify-operator/doe-notify-operator.component';
import { DoePeerReviewComponent } from './tasks/peer-review/doe-peer-review.component';

const canDeactivateDoe: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const DOE_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateDoe],
    children: [
      {
        path: 'aviation-details',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: DoeCorsiaEmissionsFormProvider }],
        loadChildren: () =>
          import('./tasks/aviation-emissions/doe-aviation-emissions.routes').then(
            (r) => r.DOE_AVIATION_EMISSIONS_ROUTES,
          ),
      },
      {
        path: 'review',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: DoeCorsiaEmissionsFormProvider }],
        canActivate: [canActivateDoeAviationEmissions],
        canDeactivate: [canDeactivateDoeAviationEmissions],
        children: [
          {
            path: 'peer-review',
            component: DoePeerReviewComponent,
            providers: [PaymentCompletedGuard],
            canActivate: [PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'notify-operator',
            data: { pageTitle: 'Notify Operator of Decision' },
            providers: [PaymentCompletedGuard],
            canActivate: [PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
            component: DoeNotifyOperatorComponent,
          },
        ],
      },
    ],
  },
];
