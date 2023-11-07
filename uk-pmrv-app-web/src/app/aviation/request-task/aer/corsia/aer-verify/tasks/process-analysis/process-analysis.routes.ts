import { Routes } from '@angular/router';

import {
  canActivateProcessAnalysis,
  canDeactivateProcessAnalysis,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/process-analysis.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_PROCESS_ANALYSIS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateProcessAnalysis],
    canDeactivate: [canDeactivateProcessAnalysis],
    children: [
      {
        path: '',
        data: { pageTitle: 'Verification activities carried out' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./process-analysis.component').then((c) => c.ProcessAnalysisComponent),
      },
      {
        path: 'strategic-analysis',
        data: { pageTitle: 'Strategic analysis and risk assessment', backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./strategic-analysis/strategic-analysis.component').then((c) => c.StrategicAnalysisComponent),
      },
      {
        path: 'data-sampling',
        data: { pageTitle: 'Data sampling', backlink: '../strategic-analysis' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./data-sampling/data-sampling.component').then((c) => c.DataSamplingComponent),
      },
      {
        path: 'compliance',
        data: { pageTitle: 'Compliance with the emissions monitoring plan', backlink: '../data-sampling' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./compliance/compliance.component').then((c) => c.ComplianceComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Process and analysis details summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
