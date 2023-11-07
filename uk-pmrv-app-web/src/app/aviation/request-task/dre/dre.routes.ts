import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { RequestTaskStore } from '../store';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import { AviationEmissionsFormProvider } from './tasks/aviation-emissions';
import {
  canActivateAviationEmissions,
  canDeactivateAviationEmissions,
} from './tasks/aviation-emissions/aviation-emissions.guards';
import { DreNotifyOperatorComponent } from './tasks/notify-operator/notify-operator.component';
import { DrePeerReviewComponent } from './tasks/peer-review/peer-review.component';

const canDeactivateDre: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const DRE_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateDre],
    children: [
      {
        path: 'aviation-details',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: AviationEmissionsFormProvider }],
        loadChildren: () =>
          import('./tasks/aviation-emissions/aviation-emissions.routes').then((r) => r.DRE_AVIATION_EMISSIONS_ROUTES),
      },
      {
        path: 'review',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: AviationEmissionsFormProvider }],
        canActivate: [canActivateAviationEmissions],
        canDeactivate: [canDeactivateAviationEmissions],
        children: [
          {
            path: 'peer-review',
            component: DrePeerReviewComponent,
            providers: [PaymentCompletedGuard],
            canActivate: [PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'notify-operator',
            providers: [PaymentCompletedGuard],
            canActivate: [PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
            component: DreNotifyOperatorComponent,
          },
        ],
      },
    ],
  },
];
