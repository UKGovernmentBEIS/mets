import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TaskGuard } from '@tasks/task.guard';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { TaskInfoGuard } from '../shared/guards/task-info.guard';
import { LegalEntityDetailsComponent } from '../shared/legal-entity-details/legal-entity-details.component';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { ApplicationTypeComponent } from './application-type/application-type.component';
import { CancelApplicationComponent } from './cancel-application/cancel-application.component';
import { CommencementComponent } from './commencement/commencement.component';
import { ConfirmResponsibilityComponent } from './confirm-responsibility/confirm-responsibility.component';
import { EtsSchemeComponent } from './ets-scheme/ets-scheme.component';
import { GasEmissionsDetailsComponent } from './gas-emissions-details/gas-emissions-details.component';
import { GasEmissionsDetailsGuard } from './gas-emissions-details/gas-emissions-details.guard';
import { ApplicationGuard } from './guards/application.guard';
import { FormGuard } from './guards/form.guard';
import { LegalEntityDetailsGuard } from './guards/legal-entity-details.guard';
import { InstallationTypeComponent } from './installation-type/installation-type.component';
import { LegalEntityDetailsOpComponent } from './legal-entity-operator/legal-entity-details-op/legal-entity-details-op.component';
import { LegalEntityDetailsOpGuard } from './legal-entity-operator/legal-entity-details-op/legal-entity-details-op.guard';
import { LegalEntityRegnoOpComponent } from './legal-entity-operator/legal-entity-regno-op/legal-entity-regno-op.component';
import { LegalEntityRegnoOpGuard } from './legal-entity-operator/legal-entity-regno-op/legal-entity-regno-op.guard';
import { LegalEntitySelectOpComponent } from './legal-entity-operator/legal-entity-select-op/legal-entity-select-op.component';
import { LegalEntitySelectOpGuard } from './legal-entity-operator/legal-entity-select-op/legal-entity-select-op.guard';
import { LegalEntitySelectRegComponent } from './legal-entity-regulator/legal-entity-select-reg/legal-entity-select-reg.component';
import { LegalEntitySelectRegGuard } from './legal-entity-regulator/legal-entity-select-reg/legal-entity-select-reg.guard';
import { OffshoreGuard } from './offshore-details/offshore.guard';
import { OffshoreDetailsComponent } from './offshore-details/offshore-details.component';
import { OnshoreGuard } from './onshore-details/onshore.guard';
import { OnshoreDetailsComponent } from './onshore-details/onshore-details.component';
import { OperatorApplicationComponent } from './operator-application/operator-application.component';
import { ReviewComponent } from './review/review.component';
import { SubmittedDecisionComponent } from './submitted-decision/submitted-decision.component';
import { SummaryComponent } from './summary/summary.component';
import { SummaryGuard } from './summary/summary.guard';
import { SummaryActionGuard } from './summary/summary-action.guard';
import { TaskListComponent } from './task-list/task-list.component';

const formRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { pageTitle: 'Apply for an installation account' },
        component: TaskListComponent,
      },
      {
        path: 'cancel',
        data: { pageTitle: 'Delete installation account creation', breadcrumb: true },
        component: CancelApplicationComponent,
      },
      {
        path: 'legal-entity-op',
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'select',
          },
          {
            path: 'select',
            data: { pageTitle: 'Select the legal entity' },
            component: LegalEntitySelectOpComponent,
            canActivate: [LegalEntitySelectOpGuard],
            canDeactivate: [PendingRequestGuard],
            resolve: { legalEntities: LegalEntitySelectOpGuard },
          },
          {
            path: 'regno',
            data: { pageTitle: 'Select the registration number' },
            component: LegalEntityRegnoOpComponent,
            canActivate: [LegalEntityRegnoOpGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'details',
            data: { pageTitle: 'Enter the legal entity details' },
            canActivate: [LegalEntityDetailsOpGuard],
            canDeactivate: [PendingRequestGuard],
            component: LegalEntityDetailsOpComponent,
          },
        ],
      },
      {
        path: 'legal-entity',
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'select',
          },
          {
            path: 'select',
            data: { pageTitle: 'Select the legal entity' },
            component: LegalEntitySelectRegComponent,
            canActivate: [LegalEntitySelectRegGuard],
            canDeactivate: [PendingRequestGuard],
            resolve: { legalEntities: LegalEntitySelectRegGuard },
          },
          {
            path: 'details',
            data: { pageTitle: 'Enter the legal entity details' },
            canActivate: [LegalEntityDetailsGuard],
            canDeactivate: [PendingRequestGuard],
            component: LegalEntityDetailsComponent,
          },
        ],
      },
      {
        path: 'installation',
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'type',
          },
          {
            path: 'type',
            data: { pageTitle: 'Select your installation type' },
            component: InstallationTypeComponent,
          },
          {
            path: 'onshore',
            data: { pageTitle: 'Enter the installation details for onshore' },
            canActivate: [OnshoreGuard],
            canDeactivate: [PendingRequestGuard],
            component: OnshoreDetailsComponent,
          },
          {
            path: 'offshore',
            data: { pageTitle: 'Enter the installation details for offshore' },
            canActivate: [OffshoreGuard],
            canDeactivate: [PendingRequestGuard],
            component: OffshoreDetailsComponent,
          },
          {
            path: 'emissions',
            data: {
              pageTitle: 'What is the location of the installation which will be producing greenhouse gas emissions?',
            },
            canActivate: [GasEmissionsDetailsGuard],
            component: GasEmissionsDetailsComponent,
          },
        ],
      },
      {
        path: 'responsibility',
        data: { pageTitle: 'Confirm responsibility for operating the installation' },
        component: ConfirmResponsibilityComponent,
      },
      {
        path: 'ets-scheme',
        data: { pageTitle: 'What emissions trading scheme will this installation be part of?' },
        canDeactivate: [PendingRequestGuard],
        component: EtsSchemeComponent,
      },
      {
        path: 'commencement',
        data: { pageTitle: 'Enter the start date of regulated activities' },
        component: CommencementComponent,
      },
      {
        path: 'type',
        data: { pageTitle: 'Confirm the type application' },
        component: ApplicationTypeComponent,
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check the information provided before submitting',
          breadcrumb: 'Installation account summary',
        },
        canActivate: [SummaryGuard],
        component: SummaryComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'application',
  },
  {
    path: 'application',
    data: { breadcrumb: 'Apply for an installation account' },
    canActivate: [FormGuard],
    children: formRoutes,
  },
  {
    path: 'summary/:actionId',
    data: { pageTitle: 'Installation account application details', breadcrumb: true },
    canActivate: [SummaryActionGuard],
    resolve: { type: SummaryActionGuard },
    component: SummaryComponent,
  },
  {
    path: 'submitted',
    data: {
      pageTitle: 'We have received your installation account application',
      breadcrumb: 'Application submitted',
    },
    component: ApplicationSubmittedComponent,
  },
  {
    path: 'submitted-decision/:actionId',
    data: {
      pageTitle: 'The regulator submitted decision of the installation account application',
      breadcrumb: 'Submitted decision',
    },
    component: SubmittedDecisionComponent,
  },
  {
    path: ':taskId',
    data: { breadcrumb: 'Review installation account application' },
    children: [
      {
        path: '',
        component: ReviewComponent,
        data: { pageTitle: 'Review installation account application' },
        canActivate: [TaskInfoGuard],
        canDeactivate: [PendingRequestGuard, TaskInfoGuard],
        resolve: { review: TaskInfoGuard },
      },
      {
        path: 'application',
        component: OperatorApplicationComponent,
        canActivate: [ApplicationGuard],
        children: formRoutes,
      },
      {
        path: 'change-assignee',
        canActivate: [TaskGuard],
        canDeactivate: [TaskGuard],
        loadChildren: () =>
          import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InstallationAccountApplicationRoutingModule {}
