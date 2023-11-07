import { inject, NgModule } from '@angular/core';
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

import { RequestsService } from 'pmrv-api';

import {
  AccountReportingStatusHistoryComponent,
  CreateAviationAccountComponent,
  CreateAviationAccountSuccessComponent,
  CreateAviationAccountSummaryComponent,
  EditAviationAccountComponent,
  ViewAviationAccountComponent,
} from './containers';
import { EditReportingStatusComponent } from './containers/edit-reporting-status';
import {
  CreateAviationAccountGuard,
  CreateAviationAccountSuccessGuard,
  CreateAviationAccountSummaryGuard,
  EditAviationAccountGuard,
} from './guards';
import { canActivateEditReportingStatus } from './guards/account-guards';
import { AviationAccountGuard } from './guards/aviation-account.guard';
import { AviationAccountReportingStatusHistoryGuard } from './guards/aviation-account-reporting-status-history.guard';
import { WorkflowGuard } from './guards/workflow.guard';
import { AviationAccountsStore } from './store';

const routes: Routes = [
  {
    path: 'accounts',
    data: { breadcrumb: 'Accounts' },
    children: [
      {
        path: '',
        component: AccountsPageComponent,
      },
      {
        path: 'create',
        data: { breadcrumb: 'Add an operator account' },
        canActivate: [CreateAviationAccountGuard],
        canDeactivate: [CreateAviationAccountGuard],
        children: [
          {
            path: '',
            data: { pageTitle: 'Aviation operator account' },
            component: CreateAviationAccountComponent,
          },
          {
            path: 'summary',
            data: { pageTitle: 'Aviation operator account summary' },
            component: CreateAviationAccountSummaryComponent,
            canActivate: [CreateAviationAccountSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'success',
            data: { pageTitle: 'You have successfully created an aviation operator account' },
            component: CreateAviationAccountSuccessComponent,
            canActivate: [CreateAviationAccountSuccessGuard],
          },
        ],
      },
      {
        path: ':accountId',
        canActivate: [AviationAccountGuard],
        data: { breadcrumb: (data) => data.accountName },
        resolve: {
          accountName: () => inject(AviationAccountsStore).getState().currentAccount.account.aviationAccount.name,
        },
        children: [
          {
            path: '',
            data: { pageTitle: 'Account' },
            component: ViewAviationAccountComponent,
            canDeactivate: [PendingRequestGuard],
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
            path: 'file-download/:fileType/:empId/:uuid',
            component: FileDownloadComponent,
          },
          {
            path: 'reports',
            data: { pageTitle: 'Reports', breadcrumb: true },
            component: ReportsComponent,
          },
          {
            path: 'edit',
            data: { pageTitle: 'Account', breadcrumb: 'Edit account' },
            component: EditAviationAccountComponent,
            canActivate: [EditAviationAccountGuard],
            canDeactivate: [PendingRequestGuard, EditAviationAccountGuard],
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
                      breadcrumb: ({ user }) => `${user.firstName} ${user.lastName}`,
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
                data: { pageTitle: 'Users, contacts and verifiers - Add user', breadcrumb: 'Add user' },
                component: OperatorAddComponent,
                canActivate: [AccountStatusGuard],
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
                data: {
                  pageTitle: 'Workflow item',
                  breadcrumb: (data) => `${data.workflowItem.id}`,
                },
                resolve: {
                  workflowItem: (route) => {
                    return inject(RequestsService).getRequestDetailsById(route.paramMap.get('request-id'));
                  },
                },
                children: [
                  {
                    path: '',
                    canActivate: [WorkflowGuard],
                    canDeactivate: [WorkflowGuard],
                    loadChildren: () =>
                      import('../../workflow-item/workflow-item.module').then((m) => m.WorkflowItemModule),
                  },
                  {
                    path: 'payment',
                    loadChildren: () => import('../../payment/payment.module').then((m) => m.PaymentModule),
                  },
                  {
                    path: 'tasks',
                    loadChildren: () => import('../request-task/request-task.module').then((m) => m.RequestTaskModule),
                  },
                  {
                    path: 'actions',
                    loadChildren: () =>
                      import('../request-action/request-action.module').then((m) => m.RequestActionModule),
                  },
                  {
                    path: 'rfi',
                    loadChildren: () => import('../../rfi/rfi.module').then((m) => m.RfiModule),
                  },
                  {
                    path: 'rde',
                    loadChildren: () => import('../../rde/rde.module').then((m) => m.RdeModule),
                  },
                ],
              },
            ],
          },
          {
            path: 'reporting-status-history',
            data: { pageTitle: 'Reporting status history', breadcrumb: true },
            component: AccountReportingStatusHistoryComponent,
            canActivate: [AviationAccountReportingStatusHistoryGuard],
            canDeactivate: [AviationAccountReportingStatusHistoryGuard],
          },
          {
            path: 'edit-reporting-status',
            canActivate: [canActivateEditReportingStatus],
            component: EditReportingStatusComponent,
          },
          {
            path: 'verification-body',
            children: [
              {
                path: 'appoint',
                data: { pageTitle: 'Users, contacts and verifiers - Appoint a verifier' },
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
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AviationAccountsRoutingModule {}
