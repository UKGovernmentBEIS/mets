import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts/accounts-regulators-site-contacts.component';
import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { MiReportsListGuard } from './core/mi-reports-list.guard';
import { CustomReportComponent } from './custom/custom.component';
import { MiReportsComponent } from './mi-reports.component';
import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks/regulator-outstanding-request-tasks.component';
import { VerificationBodiesUsersComponent } from './verification-bodies-users/verification-bodies-users.component';

const routes: Routes = [
  {
    path: '',
    component: MiReportsComponent,
    canActivate: [MiReportsListGuard],
    resolve: { miReports: MiReportsListGuard },
  },
  {
    path: 'accounts-users-contacts',
    data: { breadcrumb: 'List of accounts, users and contacts' },
    component: AccountsUsersContactsComponent,
  },
  {
    path: 'completed-work',
    data: { breadcrumb: 'Completed work' },
    component: CompletedWorkComponent,
  },
  {
    path: 'regulator-outstanding-request-tasks',
    data: { breadcrumb: 'Regulator outstanding request tasks' },
    component: RegulatorOutstandingRequestTasksComponent,
  },
  {
    path: 'accounts-regulators-sites-contacts',
    data: { breadcrumb: 'List of Accounts, Assigned Regulators and Site Contacts' },
    component: AccountsRegulatorsSiteContactsComponent,
  },
  {
    path: 'verification-bodies-users',
    data: { breadcrumb: 'List of Verification bodies and Users' },
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
