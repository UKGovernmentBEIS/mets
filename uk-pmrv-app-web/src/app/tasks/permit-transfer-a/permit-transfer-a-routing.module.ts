import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { WizardStepGuard } from './core/guards/wizard-step.guard';
import { TransferAAemReportComponent } from './submit/aem-report/aem-report.component';
import { TransferACodeComponent } from './submit/code/code.component';
import { TransferADateComponent } from './submit/date/date.component';
import { TransferAPaymentComponent } from './submit/payment/payment.component';
import { TransferAReasonComponent } from './submit/reason/reason.component';
import { SendApplicationComponent } from './submit/send-application/send-application.component';
import { SendApplicationGuard } from './submit/send-application/send-application.guard';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { TransferWaitComponent } from './wait/wait.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Full transfer of permit' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'reason',
        data: { pageTitle: 'Transfer details' },
        component: TransferAReasonComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'date',
        data: { pageTitle: 'Date of transfer', backlink: '../reason' },
        component: TransferADateComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'payment',
        data: { pageTitle: 'Transfer payment', backlink: '../date' },
        component: TransferAPaymentComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'aem-report',
        data: { pageTitle: 'AEM report', backlink: '../payment' },
        component: TransferAAemReportComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'code',
        data: { pageTitle: 'Transfer code', backlink: '../aem-report' },
        component: TransferACodeComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Details' },
        component: SummaryComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'send-application',
        data: { pageTitle: 'Send to the new operator' },
        component: SendApplicationComponent,
        canActivate: [SendApplicationGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Full transfer of permit' },
        component: TransferWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitTransferARoutingModule {}
