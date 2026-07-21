import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { isFeatureEnabled } from '@core/config/feature.guard';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts/accounts-regulators-site-contacts.component';
import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { AddCustomReportComponent } from './add-custom-report/add-custom-report.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { MiReportsListGuard } from './core/mi-reports-list.guard';
import { CustomReportComponent } from './custom/custom.component';
import { MiReportsComponent } from './mi-reports.component';
import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks/regulator-outstanding-request-tasks.component';
import { UsersForServiceAuthorityComponent } from './users-for-service-authority/users-for-service-authority.component';
import { VerificationBodiesUsersComponent } from './verification-bodies-users/verification-bodies-users.component';
import { ViewCustomReportComponent } from './view-custom-report/view-custom-report.component';
import { viewCustomReportResolver } from './view-custom-report/view-custom-report.resolver';

const routes: Routes = [
  {
    path: '',
    component: MiReportsComponent,
    canActivate: [MiReportsListGuard],
    resolve: { standardReports: MiReportsListGuard },
  },
  {
    path: 'add-custom-report',
    data: { breadcrumb: 'Add a custom report', backlink: '../' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled')],
    canDeactivate: [PendingRequestGuard],
    component: AddCustomReportComponent,
  },
  {
    path: 'view-custom-report/:id',
    data: { breadcrumb: 'View custom report', backlink: '../../' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled')],
    canDeactivate: [PendingRequestGuard],
    resolve: { report: viewCustomReportResolver },
    component: ViewCustomReportComponent,
  },
  {
    path: 'accounts-users-contacts',
    data: { breadcrumb: 'List of accounts, users and contacts', backlink: '../' },
    component: AccountsUsersContactsComponent,
  },
  {
    path: 'completed-work',
    data: { breadcrumb: 'Completed work', backlink: '../' },
    component: CompletedWorkComponent,
  },
  {
    path: 'regulator-outstanding-request-tasks',
    data: { breadcrumb: 'Regulator outstanding request tasks', backlink: '../' },
    component: RegulatorOutstandingRequestTasksComponent,
  },
  {
    path: 'user-report-entries',
    data: { breadcrumb: 'List of users', backlink: '../' },
    component: UsersForServiceAuthorityComponent,
  },
  {
    path: 'accounts-regulators-sites-contacts',
    data: { breadcrumb: 'List of Accounts, Assigned Regulators and Site Contacts', backlink: '../' },
    component: AccountsRegulatorsSiteContactsComponent,
  },
  {
    path: 'verification-bodies-users',
    data: { breadcrumb: 'List of Verification bodies and Users', backlink: '../' },
    component: VerificationBodiesUsersComponent,
  },
  {
    path: 'custom',
    data: { breadcrumb: 'Custom SQL report' },
    component: CustomReportComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MiReportsRoutingModule {}
