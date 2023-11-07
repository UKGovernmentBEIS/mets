import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { LocationStateFormComponent } from '@aviation/shared/components/location-state-form/location-state-form.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAccountsRoutingModule } from './aviation-accounts-routing.module';
import {
  AccountReportingStatusComponent,
  AccountReportingStatusHistoryListComponent,
  AviationAccountClosedComponent,
  AviationAccountFormComponent,
  AviationAccountSummaryInfoComponent,
} from './components';
import {
  AccountReportingStatusHistoryComponent,
  AviationAccountDetailsComponent,
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
import { AviationAccountGuard } from './guards/aviation-account.guard';
import { AviationAccountReportingStatusHistoryGuard } from './guards/aviation-account-reporting-status-history.guard';
import { EtsNamePipe } from './pipes';
import { AccountReportingStatusPipe } from './pipes/account-reporting-status.pipe';
import { AviationAccountFormProvider } from './services';
import { AviationAccountsStore } from './store';

@NgModule({
  declarations: [
    AccountReportingStatusComponent,
    AccountReportingStatusHistoryComponent,
    AccountReportingStatusHistoryListComponent,
    AccountReportingStatusPipe,
    AviationAccountClosedComponent,
    AviationAccountDetailsComponent,
    AviationAccountFormComponent,
    AviationAccountSummaryInfoComponent,
    CreateAviationAccountComponent,
    CreateAviationAccountSuccessComponent,
    CreateAviationAccountSummaryComponent,
    EditAviationAccountComponent,
    EditReportingStatusComponent,
    EtsNamePipe,
    ViewAviationAccountComponent,
  ],
  imports: [AviationAccountsRoutingModule, CommonModule, LocationStateFormComponent, SharedModule],
  providers: [
    AviationAccountFormProvider,
    AviationAccountGuard,
    AviationAccountReportingStatusHistoryGuard,
    AviationAccountsStore,
    CreateAviationAccountGuard,
    CreateAviationAccountSuccessGuard,
    CreateAviationAccountSummaryGuard,
    EditAviationAccountGuard,
  ],
})
export class AviationAccountsModule {}
