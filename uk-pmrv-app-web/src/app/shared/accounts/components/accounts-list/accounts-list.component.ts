import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { AccountSearchResult } from '@shared/accounts';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountsListComponent {
  @Input() accounts: AccountSearchResult[];
  @Output() readonly selectAccount = new EventEmitter<AccountSearchResult>();

  clickAccount(event: MouseEvent, account: AccountSearchResult) {
    event.preventDefault();
    this.selectAccount.emit(account);
  }
}
