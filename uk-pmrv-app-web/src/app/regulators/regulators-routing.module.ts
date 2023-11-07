import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { DeleteComponent } from './delete/delete.component';
import { DeleteResolver } from './delete/delete.resolver';
import { DetailsComponent } from './details/details.component';
import { DetailsResolver } from './details/details.resolver';
import { PermissionsResolver } from './details/permissions.resolver';
import { DeleteComponent as ExternalContactsDeleteComponent } from './external-contacts/delete/delete.component';
import { DeleteGuard } from './external-contacts/delete/delete.guard';
import { DetailsComponent as ExternalContactsDetailsComponent } from './external-contacts/details/details.component';
import { DetailsGuard } from './external-contacts/details/details.guard';
import { SignatureFileDownloadComponent } from './file-download/signature-file-download.component';
import { RegulatorsComponent } from './regulators.component';
import { RegulatorsGuard } from './regulators.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Regulator users and contacts' },
    component: RegulatorsComponent,
    resolve: { regulators: RegulatorsGuard },
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add',
    data: { pageTitle: 'Add a new user', breadcrumb: true },
    component: DetailsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':userId',
    children: [
      {
        path: '',
        data: {
          pageTitle: 'User details',
          breadcrumb: ({ user }) => `${user.firstName} ${user.lastName}`,
        },
        pathMatch: 'full',
        component: DetailsComponent,
        resolve: {
          user: DetailsResolver,
          permissions: PermissionsResolver,
        },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: {
          pageTitle: 'Confirm that this user account will be deleted',
          breadcrumb: ({ user }) => `Delete ${user.firstName} ${user.lastName}`,
        },
        component: DeleteComponent,
        resolve: { user: DeleteResolver },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'file-download/:uuid',
        component: SignatureFileDownloadComponent,
      },
    ],
  },
  {
    path: 'file-download/:uuid',
    component: SignatureFileDownloadComponent,
  },
  {
    path: 'external-contacts',
    children: [
      {
        path: 'add',
        data: { pageTitle: 'Add an external contact', breadcrumb: true },
        component: ExternalContactsDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':userId',
        children: [
          {
            path: '',
            pathMatch: 'full',
            data: { pageTitle: 'External contact details', breadcrumb: true },
            component: ExternalContactsDetailsComponent,
            canActivate: [DetailsGuard],
            resolve: { contact: DetailsGuard },
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete',
            data: {
              pageTitle: 'Confirm that this external contact will be deleted',
              breadcrumb: 'Delete external contact',
            },
            component: ExternalContactsDeleteComponent,
            canActivate: [DeleteGuard],
            resolve: { contact: DeleteGuard },
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RegulatorRoutingModule {}
