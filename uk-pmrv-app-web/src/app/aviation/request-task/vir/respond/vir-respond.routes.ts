import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemResolver } from '@aviation/request-task/vir/reference-item.resolver';
import {
  canActivateRespondItem,
  canDeactivateRespondItem,
} from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item.guards';
import { RespondItemFormProvider } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item-form.provider';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

export const VIR_RESPOND_ROUTES: Routes = [
  {
    path: ':id',
    resolve: { verificationDataItem: ReferenceItemResolver },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: RespondItemFormProvider }],
    canActivate: [canActivateRespondItem],
    canDeactivate: [canDeactivateRespondItem],
    children: [
      {
        path: '',
        data: { pageTitle: 'Follow-up response to an item' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./tasks/respond-item/respond-item.component').then((c) => c.RespondItemComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        canActivate: [canActivateSummaryPage],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./tasks/respond-item/summary/summary.component').then((c) => c.SummaryComponent),
      },
      {
        path: 'send-report',
        data: { pageTitle: 'Send report' },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/respond-item/send-report/send-report.component').then((c) => c.SendReportComponent),
      },
    ],
  },
];
