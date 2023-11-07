import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemResolver } from '@aviation/request-task/vir/reference-item.resolver';
import {
  canActivateReferenceItem,
  canDeactivateActivateReferenceItem,
} from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item.guards';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

export const VIR_SUBMIT_ROUTES: Routes = [
  {
    path: 'send-report',
    data: { pageTitle: 'Send report' },
    canDeactivate: [PendingRequestGuard],
    loadComponent: () => import('./tasks/send-report/send-report.component').then((c) => c.SendReportComponent),
  },
  {
    path: ':id',
    resolve: { verificationDataItem: ReferenceItemResolver },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ReferenceItemFormProvider }],
    canActivate: [canActivateReferenceItem],
    canDeactivate: [canDeactivateActivateReferenceItem],
    children: [
      {
        path: '',
        data: { pageTitle: 'Respond to an item' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/reference-item/reference-item.component').then((c) => c.ReferenceItemComponent),
      },
      {
        path: 'upload-evidence-question',
        data: { pageTitle: 'Upload evidence question' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/reference-item/upload-evidence-question/upload-evidence-question.component').then(
            (c) => c.UploadEvidenceQuestionComponent,
          ),
      },
      {
        path: 'upload-evidence-files',
        data: { pageTitle: 'Upload evidence files' },
        canActivate: [canActivateTaskForm],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/reference-item/upload-evidence-files/upload-evidence-files.component').then(
            (c) => c.UploadEvidenceFilesComponent,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        canActivate: [canActivateSummaryPage],
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./tasks/reference-item/summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
