import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { UserState } from '@core/store';

import { AviationAccountDetails } from '../../store';

@Component({
  selector: 'app-account-reporting-status',
  templateUrl: './account-reporting-status.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountReportingStatusComponent {
  @Input() accountInfo: AviationAccountDetails;
  @Input() userRoleType: UserState['roleType'];
}
