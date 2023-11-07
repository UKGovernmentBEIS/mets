import { Routes } from '@angular/router';

import { canActivateSendReportToOperator } from './send-report-to-operator.guard';

export const AER_VERIFY_SEND_REPORT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [canActivateSendReportToOperator],
        loadComponent: () => import('./send-report-to-operator-page/send-report-to-operator-page.component'),
      },
      {
        path: 'confirmation-operator',
        loadComponent: () =>
          import('./send-report-to-operator-confirmation/send-report-to-operator-confirmation.component'),
      },
    ],
  },
];
