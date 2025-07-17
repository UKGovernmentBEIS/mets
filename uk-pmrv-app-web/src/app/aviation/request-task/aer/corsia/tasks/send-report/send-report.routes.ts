import { Routes } from '@angular/router';

import {
  canActivateSendReportRegulator,
  canActivateSendReportVerifier,
} from '@aviation/request-task/aer/corsia/tasks/send-report/send-report.guards';

export const AER_CORSIA_SEND_REPORT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'verifier',
        data: { pageTitle: 'Send report for verification' },
        canActivate: [canActivateSendReportVerifier],
        loadComponent: () => import('./send-report-verifier').then((c) => c.SendReportVerifierComponent),
      },
      {
        path: 'regulator',
        data: { pageTitle: 'Send report to regulator' },
        canActivate: [canActivateSendReportRegulator],
        loadComponent: () => import('./send-report-regulator').then((c) => c.SendReportRegulatorComponent),
      },
    ],
  },
];
