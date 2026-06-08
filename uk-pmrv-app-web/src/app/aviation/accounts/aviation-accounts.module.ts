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
import { AviationAccountDetailsListComponent } from './components/aviation-account-details-history-list/aviation-account-details-history-list.component';
import {
  AccountReportingStatusHistoryComponent,
  AviationAccountDetailsComponent,
  CreateAviationAccountComponent,
  CreateAviationAccountSuccessComponent,
  CreateAviationAccountSummaryComponent,
  EditAviationAccountComponent,
  EditCommencementDateAviationAccountComponent,
  ViewAviationAccountComponent,
} from './containers';
import { AccountDetailsHistoryComponent } from './containers/aviation-account-details-history/aviation-account-details-history.component';
import { EditFyroSummaryComponent } from './containers/edit-fyro-summary/edit-fyro-summary.component';
import { EditReportingStatusComponent } from './containers/edit-reporting-status';
import { EditReportingStatusSummaryComponent } from './containers/edit-reporting-status-summary/edit-reporting-status-summary.component';
import {
  CreateAviationAccountGuard,
  CreateAviationAccountSuccessGuard,
  CreateAviationAccountSummaryGuard,
  EditAviationAccountGuard,
} from './guards';
import { AviationAccountDetailsHistoryGuard } from './guards/account-details-history-category.guard';
import { AviationAccountGuard } from './guards/aviation-account.guard';
import { AviationAccountReportingStatusHistoryGuard } from './guards/aviation-account-reporting-status-history.guard';
import { EtsNamePipe } from './pipes';
import { AviationAccoundDetailsHistoryCategoryPipe } from './pipes/account-details-history-category.pipe';
import { AccountReportingStatusPipe } from './pipes/account-reporting-status.pipe';
import { AviationAccountFormProvider } from './services';
import { AviationAccountsStore } from './store';

@NgModule({
  imports: [
    AccountDetailsHistoryComponent,
    AviationAccountDetailsListComponent,
    AviationAccoundDetailsHistoryCategoryPipe,
    AviationAccountsRoutingModule,
    CommonModule,
    LocationStateFormComponent,
    SharedModule,
  ],
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
    EditCommencementDateAviationAccountComponent,
    EditReportingStatusComponent,
    EditReportingStatusSummaryComponent,
    EditFyroSummaryComponent,
    EtsNamePipe,
    ViewAviationAccountComponent,
  ],
  providers: [
    AviationAccountDetailsHistoryGuard,
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
