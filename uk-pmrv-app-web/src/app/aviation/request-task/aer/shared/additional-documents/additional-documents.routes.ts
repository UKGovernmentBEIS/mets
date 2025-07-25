import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../guards';
import { canActivateAdditionalDocuments, canDeactivateAdditionalDocuments } from './additional-documents.guards';

export const AER_ADDITIONAL_DOCUMENTS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAdditionalDocuments],
    canDeactivate: [canDeactivateAdditionalDocuments],
    children: [
      {
        path: '',
        data: { pageTitle: 'Additional documents and information' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./additional-documents-page').then((c) => c.AdditionalDocumentsPageComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Additional documents and information' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./additional-documents-summary').then((c) => c.AdditionalDocumentsSummaryComponent),
      },
    ],
  },
];
