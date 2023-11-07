import { Injectable } from '@angular/core';

import { Store } from '@core/store/store';
import { Paging } from '@shared/model';

import { AccountSearchResultsInfoDTO, AviationAccountSearchResultsInfoDTO } from 'pmrv-api';

import { AccountsState, initialState } from './accounts.state';

@Injectable()
export class AccountsStore extends Store<AccountsState> {
  constructor() {
    super(initialState);
  }

  setSearchTerm(searchTerm: string) {
    this.setState({ ...this.getState(), searchTerm });
  }

  setSearchErrorSummaryVisible(searchErrorSummaryVisible: boolean) {
    this.setState({ ...this.getState(), searchErrorSummaryVisible });
  }

  setAccounts(accounts: AccountSearchResultsInfoDTO[] | AviationAccountSearchResultsInfoDTO[]) {
    this.setState({ ...this.getState(), accounts });
  }

  setTotal(total: number) {
    this.setState({ ...this.getState(), total });
  }

  setPaging(paging: Paging) {
    this.setState({ ...this.getState(), paging });
  }
}
