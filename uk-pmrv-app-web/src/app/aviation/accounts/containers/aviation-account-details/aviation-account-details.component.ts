import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AuthStore, selectUserRoleType } from '@core/store/auth';

import { AviationAccountsStore, selectAccountEmp, selectAccountInfo } from '../../store';

@Component({
  selector: 'app-aviation-account-details',
  templateUrl: './aviation-account-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAccountDetailsComponent {
  @Input() currentTab: string;
  accountInfo$ = this.store.pipe(selectAccountInfo);
  accountEmp$ = this.store.pipe(selectAccountEmp);
  userRoleType$ = this.authStore.pipe(selectUserRoleType);

  constructor(private readonly store: AviationAccountsStore, readonly authStore: AuthStore) {}
}
