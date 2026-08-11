import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts/accounts-regulators-site-contacts.component';
import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { AddCustomReportComponent } from './add-custom-report/add-custom-report.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { CustomReportComponent } from './custom/custom.component';
import { DeleteCustomReportComponent } from './delete-custom-report/delete-custom-report.component';
import { EditCustomReportComponent } from './edit-custom-report/edit-custom-report.component';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsRoutingModule } from './mi-reports-routing.module';
import { AuthorityStatusPipe } from './pipes/authority-status.pipe';
import { UserRoleAllTypesPipe } from './pipes/user-role-all-types.pipe';
import { VerificationBodyStatusPipe } from './pipes/verification-body-status.pipe';
import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks/regulator-outstanding-request-tasks.component';
import { ReportHistoryComponent } from './report-history/report-history.component';
import { CustomReportPreviewComponent } from './shared/custom-report-preview/custom-report-preview.component';
import { MiReportsStore } from './store';
import { UsersForServiceAuthorityComponent } from './users-for-service-authority/users-for-service-authority.component';
import { VerificationBodiesUsersComponent } from './verification-bodies-users/verification-bodies-users.component';
import { ViewCustomReportComponent } from './view-custom-report/view-custom-report.component';

@NgModule({
  imports: [CommonModule, MiReportsRoutingModule, RouterModule, SharedModule],
  declarations: [
    AccountsRegulatorsSiteContactsComponent,
    AccountsUsersContactsComponent,
    AddCustomReportComponent,
    AuthorityStatusPipe,
    CompletedWorkComponent,
    CustomReportComponent,
    CustomReportPreviewComponent,
    DeleteCustomReportComponent,
    EditCustomReportComponent,
    MiReportsComponent,
    RegulatorOutstandingRequestTasksComponent,
    ReportHistoryComponent,
    UsersForServiceAuthorityComponent,
    UserRoleAllTypesPipe,
    VerificationBodiesUsersComponent,
    VerificationBodyStatusPipe,
    ViewCustomReportComponent,
  ],
  providers: [MiReportsStore],
})
export class MiReportsModule {}
