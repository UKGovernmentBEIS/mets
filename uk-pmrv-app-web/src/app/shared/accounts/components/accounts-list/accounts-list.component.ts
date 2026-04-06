import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { AccountSearchResult } from '@shared/accounts';

@Component({
  selector: 'app-accounts-list',
  standalone: false,
  templateUrl: './accounts-list.component.html',
  styles: `
    .account-metadata {
      padding: 0;
    }
    .account-metadata-item {
      display: inline-block;
      list-style: none;
      padding-right: 20px;
      margin: 0px;
    }
  `,
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
