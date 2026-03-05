import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts/accounts-regulators-site-contacts.component';
import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { CustomReportComponent } from './custom/custom.component';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsRoutingModule } from './mi-reports-routing.module';
import { AuthorityStatusPipe } from './pipes/authority-status.pipe';
import { VerificationBodyStatusPipe } from './pipes/verification-body-status.pipe';
import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks/regulator-outstanding-request-tasks.component';
import { VerificationBodiesUsersComponent } from './verification-bodies-users/verification-bodies-users.component';

@NgModule({
  declarations: [
    AccountsRegulatorsSiteContactsComponent,
    AccountsUsersContactsComponent,
    AuthorityStatusPipe,
    CompletedWorkComponent,
    CustomReportComponent,
    MiReportsComponent,
    RegulatorOutstandingRequestTasksComponent,
    VerificationBodiesUsersComponent,
    VerificationBodyStatusPipe,
  ],
  imports: [CommonModule, MiReportsRoutingModule, RouterModule, SharedModule],
})
export class MiReportsModule {}
