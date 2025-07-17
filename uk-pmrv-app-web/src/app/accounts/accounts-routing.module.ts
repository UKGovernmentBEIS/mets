import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import {
  AccountNoteComponent,
  AccountsPageComponent,
  AccountStatusGuard,
  AddComponent as OperatorAddComponent,
  AppointComponent,
  AppointGuard,
  DeleteAccountNoteComponent,
  DeleteComponent as OperatorDeleteComponent,
  DeleteGuard as OperatorDeleteGuard,
  DetailsComponent as OperatorDetailsComponent,
  DetailsGuard as OperatorDetailsGuard,
  ProcessActionsComponent,
  ReplaceGuard,
  ReportsComponent,
} from '@shared/accounts';
import { NoteFileDownloadComponent } from '@shared/components/note-file-download/note-file-download.component';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { RoleTypeGuard } from '@shared/guards/role-type.guard';
import { LegalEntityDetailsComponent } from '@shared/legal-entity-details';

import { InstallationAccountApplicationGuard } from '../installation-account-application/installation-account-application.guard';
import { AccountComponent } from './account.component';
import { AccountGuard } from './account.guard';
import { AuditYearComponent } from './audit-year/audit-year.component';
import { AddressComponent } from './edit/address/address.component';
import { FaStatusComponent } from './edit/fa-status/fa-status.component';
import { NameComponent } from './edit/name/name.component';
import { RegistryIdComponent } from './edit/registry-id/registry-id.component';
import { SiteNameComponent } from './edit/site-name/site-name.component';
import { SopIdComponent } from './edit/sop-id/sop-id.component';
import { TriggerAirComponent } from './trigger-air/trigger-air.component';
import { TriggerDoalComponent } from './trigger-doal/trigger-doal.component';
import { AerMarkAsNotRequiredComponent } from './workflows/aer-mark-as-not-required/aer-mark-as-not-required.component';
import { MarkAsNotRequiredGuard } from './workflows/aer-mark-as-not-required/mark-as-not-required-guard.service';
import { AerReinitializeComponent } from './workflows/aer-reinitialize/aer-reinitialize.component';
import { WorkflowGuard } from './workflows/workflow.guard';

const routes: Routes = [
  {
    path: '',
    component: AccountsPageComponent,
  },
  {
    path: ':accountId',
    canActivate: [AccountGuard],
    canDeactivate: [AccountGuard],
    resolve: { accountPermit: AccountGuard },
    runGuardsAndResolvers: 'always',
    data: { breadcrumb: { resolveText: ({ accountPermit }) => accountPermit.account.name, skipLink: false } },
    children: [
      {
        path: '',
        data: { pageTitle: 'Account' },
        component: AccountComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'permit/:permitId/:fileType/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: 'verification-body',
        children: [
          {
            path: 'appoint',
            data: {
              pageTitle: 'Users, contacts and verifiers - Appoint a verifier',
              breadcrumb: true,
            },
            component: AppointComponent,
            canActivate: [AppointGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'replace',
            data: {
              pageTitle: 'Users, contacts and verifiers - Replace a verifier',
              breadcrumb: true,
            },
            component: AppointComponent,
            canActivate: [ReplaceGuard],
            canDeactivate: [PendingRequestGuard],
            resolve: { verificationBody: ReplaceGuard },
          },
        ],
      },
      {
        path: 'users',
        children: [
          {
            path: ':userId',
            children: [
              {
                path: '',
                pathMatch: 'full',
                data: {
                  pageTitle: 'User details',
                  breadcrumb: ({ user }) => `${user.firstName} ${user.lastName}`,
                },
                component: OperatorDetailsComponent,
                canActivate: [OperatorDetailsGuard],
                canDeactivate: [PendingRequestGuard],
                resolve: { user: OperatorDetailsGuard },
              },
              {
                path: 'delete',
                data: {
                  pageTitle: 'Confirm that this user account will be deleted',
                  breadcrumb: ({ user }) => `Delete ${user.firstName} ${user.lastName}`,
                },
                component: OperatorDeleteComponent,
                canActivate: [OperatorDeleteGuard],
                canDeactivate: [PendingRequestGuard],
                resolve: { user: OperatorDeleteGuard },
              },
            ],
          },
          {
            path: 'add/:userType',
            data: {
              pageTitle: 'Users, contacts and verifiers - Add user',
              breadcrumb: true,
            },
            component: OperatorAddComponent,
            canActivate: [AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'edit',
        children: [
          {
            path: 'name',
            data: {
              pageTitle: 'Edit installation name',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} installation name`,
            },
            component: NameComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'registry-id',
            data: {
              pageTitle: 'Edit UK ETS Registry ID',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} UK ETS Registry ID`,
            },
            component: RegistryIdComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'sop-id',
            data: {
              pageTitle: 'Edit SOP ID',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} SOP ID`,
            },
            component: SopIdComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'site-name',
            data: {
              pageTitle: 'Edit site name',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} site name`,
            },
            component: SiteNameComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'legal-entity',
            data: {
              pageTitle: 'Edit legal entity',
              roleTypeGuards: 'REGULATOR',
              context: 'edit',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} legal entity`,
            },
            component: LegalEntityDetailsComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'address',
            data: {
              pageTitle: 'Edit address',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} address`,
            },
            component: AddressComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'fa-status',
            data: {
              pageTitle: 'Edit free allocation status',
              roleTypeGuards: 'REGULATOR',
              breadcrumb: ({ accountPermit }) => `Edit ${accountPermit.account.name} free allocation status`,
            },
            component: FaStatusComponent,
            canActivate: [RoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'process-actions',
        data: { pageTitle: 'Account process actions', breadcrumb: true },
        component: ProcessActionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'workflows',
        children: [
          {
            path: ':request-id',
            data: { pageTitle: 'Workflow item', breadcrumb: ({ requestId }) => requestId },
            resolve: { requestId: (route) => route.paramMap.get('request-id') },
            children: [
              {
                path: '',
                canActivate: [WorkflowGuard],
                canDeactivate: [WorkflowGuard],
                loadChildren: () => import('../workflow-item/workflow-item.module').then((m) => m.WorkflowItemModule),
              },
              {
                path: 'aer-reinitialize',
                data: {
                  pageTitle: 'Return the annual emissions report to the operator',
                  breadcrumb: 'Return AER to operator',
                },
                component: AerReinitializeComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'aer-mark-as-not-required',
                data: {
                  pageTitle: 'Return the annual emissions report to the operator',
                  breadcrumb: 'Mark work flow as not required',
                },
                component: AerMarkAsNotRequiredComponent,
                canActivate: [MarkAsNotRequiredGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'payment',
                loadChildren: () => import('../payment/payment.module').then((m) => m.PaymentModule),
              },
              {
                path: 'permit-issuance',
                loadChildren: () =>
                  import('../permit-issuance/permit-issuance.module').then((m) => m.PermitIssuanceModule),
              },
              {
                path: 'installation-account',
                canActivate: [InstallationAccountApplicationGuard],
                loadChildren: () =>
                  import('../installation-account-application/installation-account-application.module').then(
                    (m) => m.InstallationAccountApplicationModule,
                  ),
              },
              {
                path: 'tasks',
                loadChildren: () => import('../tasks/tasks.module').then((m) => m.TasksModule),
              },
              {
                path: 'actions',
                loadChildren: () => import('../actions/actions.module').then((m) => m.ActionsModule),
              },
              {
                path: 'permit-variation',
                loadChildren: () =>
                  import('../permit-variation/permit-variation.module').then((m) => m.PermitVariationModule),
              },
              {
                path: 'permit-transfer',
                loadChildren: () =>
                  import('../permit-transfer/permit-transfer.module').then((m) => m.PermitTransferModule),
              },
              {
                path: 'permit-revocation',
                loadChildren: () =>
                  import('../permit-revocation/permit-revocation.module').then((m) => m.PermitRevocationModule),
              },
              {
                path: 'permit-surrender',
                loadChildren: () =>
                  import('../permit-surrender/permit-surrender.module').then((m) => m.PermitSurrenderModule),
              },
              {
                path: 'rfi',
                loadChildren: () => import('../rfi/rfi.module').then((m) => m.RfiModule),
              },
              {
                path: 'rde',
                loadChildren: () => import('../rde/rde.module').then((m) => m.RdeModule),
              },
            ],
          },
        ],
      },
      {
        path: 'reports',
        data: { pageTitle: 'Reports', breadcrumb: true },
        component: ReportsComponent,
      },
      {
        path: 'notes',
        children: [
          {
            path: 'add',
            data: { pageTitle: 'Add a note', breadcrumb: true },
            component: AccountNoteComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':noteId/edit',
            data: { pageTitle: 'Edit a note', breadcrumb: true },
            component: AccountNoteComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':noteId/delete',
            data: { pageTitle: 'Delete a note', breadcrumb: true },
            component: DeleteAccountNoteComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'file-download/:uuid',
        component: NoteFileDownloadComponent,
      },
      {
        path: 'trigger-air',
        data: { pageTitle: 'Trigger annual improvement report', breadcrumb: true },
        component: TriggerAirComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'trigger-doal',
        data: { pageTitle: 'Start a determination of activity level change', breadcrumb: true },
        component: TriggerDoalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'audit-year',
        data: { pageTitle: 'Which year does the audit relate to?', breadcrumb: true },
        component: AuditYearComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountsRoutingModule {}
