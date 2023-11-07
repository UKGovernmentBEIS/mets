import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../guards';
import { canActivateAdditionalDocuments, canDeactivateAdditionalDocuments } from './additional-documents.guards';

export const EMP_ADDITIONAL_DOCUMENTS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAdditionalDocuments],
    canDeactivate: [canDeactivateAdditionalDocuments],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./additional-documents-page').then((c) => c.AdditionalDocumentsPageComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Additional documents summary' },
        loadComponent: () =>
          import('./additional-documents-summary').then((c) => c.AdditionalDocumentsSummaryComponent),
      },
    ],
  },
];
