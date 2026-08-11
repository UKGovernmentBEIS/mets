import { NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, RouterModule, Routes } from '@angular/router';

import { isFeatureEnabled } from '@core/config/feature.guard';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts/accounts-regulators-site-contacts.component';
import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { AddCustomReportComponent } from './add-custom-report/add-custom-report.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { MiReportsListGuard } from './core/mi-reports-list.guard';
import { canManageCustomReports } from './core/mi-reports-permission.guard';
import { CustomReportComponent } from './custom/custom.component';
import { DeleteCustomReportComponent } from './delete-custom-report/delete-custom-report.component';
import { EditCustomReportComponent } from './edit-custom-report/edit-custom-report.component';
import { MiReportsComponent } from './mi-reports.component';
import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks/regulator-outstanding-request-tasks.component';
import { ReportHistoryComponent } from './report-history/report-history.component';
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
    data: { hideBreadcrumb: true, backlink: '../' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled'), canManageCustomReports()],
    canDeactivate: [PendingRequestGuard],
    component: AddCustomReportComponent,
  },
  {
    path: 'edit-custom-report/:id',
    data: { hideBreadcrumb: true, backlink: ({ backlinkUrl }: { backlinkUrl: string }) => backlinkUrl },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled'), canManageCustomReports()],
    canDeactivate: [PendingRequestGuard],
    resolve: {
      report: viewCustomReportResolver,
      backlinkUrl: (route: ActivatedRouteSnapshot) => `../../view-custom-report/${route.paramMap.get('id')}`,
    },
    component: EditCustomReportComponent,
  },
  {
    path: 'view-custom-report/:id',
    data: { breadcrumb: 'View custom report' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled')],
    canDeactivate: [PendingRequestGuard],
    resolve: { report: viewCustomReportResolver },
    component: ViewCustomReportComponent,
  },
  {
    path: 'view-custom-report/:id/history',
    data: { breadcrumb: 'Report history' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled')],
    component: ReportHistoryComponent,
  },
  {
    path: 'view-custom-report/:id/delete',
    data: { breadCrumb: false, backlink: '../' },
    canMatch: [isFeatureEnabled('reportingImprovementsEnabled'), canManageCustomReports()],
    canDeactivate: [PendingRequestGuard],
    component: DeleteCustomReportComponent,
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
