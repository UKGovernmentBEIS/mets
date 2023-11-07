import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAccountReportingStatusHistoryDTO } from 'pmrv-api';

@Component({
  selector: 'app-account-reporting-status-history-list',
  templateUrl: './account-reporting-status-history-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountReportingStatusHistoryListComponent {
  @Input() history: AviationAccountReportingStatusHistoryDTO[];
}
