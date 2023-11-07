import { Routes } from '@angular/router';

import { SendReportGuard } from './send-report-page/send-report.guard';
import { VerificationGuard } from './send-report-verification/send-report-verification.guard';

export const AER_SEND_REPORT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [SendReportGuard],
        loadComponent: () => import('./send-report-page').then((c) => c.SendReportPageComponent),
      },
      {
        path: 'verification',
        canActivate: [VerificationGuard],
        loadComponent: () => import('./send-report-verification').then((c) => c.SendReportVerificationComponent),
      },
      {
        path: 'regulator',
        loadComponent: () => import('./send-report-regulator').then((c) => c.SendReportRegulatorComponent),
      },
      {
        path: 'confirmation-regulator',
        loadComponent: () => import('./confirmation-regulator').then((c) => c.ConfirmationRegulatorComponent),
      },
      {
        path: 'confirmation-verifier',
        loadComponent: () => import('./confirmation-verifier').then((c) => c.ConfirmationVerifierComponent),
      },
    ],
  },
];
