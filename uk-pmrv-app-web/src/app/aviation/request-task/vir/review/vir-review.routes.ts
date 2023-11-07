import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemResolver } from '@aviation/request-task/vir/reference-item.resolver';
import {
  canActivateCreateSummary,
  canDeactivateCreateSummary,
} from '@aviation/request-task/vir/review/tasks/create-summary/create-summary.guards';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import {
  canActivateRecommendationResponseItem,
  canDeactivateRecommendationResponseItem,
} from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item.guards';
import { RecommendationResponseItemFormProvider } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item-form.provider';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

export const VIR_REVIEW_ROUTES: Routes = [
  {
    path: 'send-report',
    data: { pageTitle: 'Send report' },
    canDeactivate: [PendingRequestGuard],
    loadComponent: () => import('./tasks/send-report/send-report.component').then((c) => c.SendReportComponent),
  },
  {
    path: 'create-summary',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: CreateSummaryFormProvider }],
    canActivate: [canActivateCreateSummary],
    canDeactivate: [canDeactivateCreateSummary],
    children: [
      {
        path: '',
        data: { pageTitle: 'Create report summary' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/create-summary/create-summary.component').then((c) => c.CreateSummaryComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        canActivate: [canActivateSummaryPage],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./tasks/create-summary/summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
  {
    path: ':id',
    resolve: { verificationDataItem: ReferenceItemResolver },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: RecommendationResponseItemFormProvider }],
    canActivate: [canActivateRecommendationResponseItem],
    canDeactivate: [canDeactivateRecommendationResponseItem],
    children: [
      {
        path: '',
        data: { pageTitle: 'Respond to an item' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/recommendation-response-item/recommendation-response-item.component').then(
            (c) => c.RecommendationResponseItemComponent,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        canActivate: [canActivateSummaryPage],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/recommendation-response-item/summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
