import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { Store } from '@core/store';
import { Paging } from '@shared/model';
import produce from 'immer';

import {
  AviationAccountCreationDTO,
  AviationAccountEmpDTO,
  AviationAccountReportingStatusHistoryCreationDTO,
  AviationAccountReportingStatusHistoryDTO,
  AviationAccountReportingStatusService,
  AviationAccountsService,
  AviationAccountUpdateService,
} from 'pmrv-api';

import {
  AviationAccountsState,
  initialCreateAccountState,
  initialCurrentAccountState,
  initialState,
} from './aviation-accounts.state';

@Injectable()
export class AviationAccountsStore extends Store<AviationAccountsState> {
  constructor(
    private readonly service: AviationAccountsService,
    private readonly reportingStatusService: AviationAccountReportingStatusService,
    private readonly aviationAccountUpdateService: AviationAccountUpdateService,
    private readonly pendingRequestService: PendingRequestService,
  ) {
    super(initialState);
  }

  setIsInitiallySubmitted(isInitiallySubmitted: boolean) {
    const state = this.getState();
    this.setState({
      ...state,
      createAccount: {
        ...state.createAccount,
        isInitiallySubmitted,
      },
    });
  }

  setIsSubmitted(isSubmitted: boolean) {
    const state = this.getState();
    this.setState({
      ...state,
      createAccount: {
        ...state.createAccount,
        isSubmitted,
      },
    });
  }

  setNewAccount(newAccount: AviationAccountCreationDTO) {
    const state = this.getState();
    this.setState({
      ...state,
      createAccount: {
        ...state.createAccount,
        newAccount,
      },
    });
  }

  setCurrentAccount(currentAccount: AviationAccountEmpDTO) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        account: currentAccount,
      },
    });
  }

  resetCurrentAccount(): void {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: null,
    });
  }

  resetCreateAccount() {
    const state = this.getState();
    this.setState({
      ...state,
      createAccount: initialCreateAccountState,
    });
  }

  setReportingStatusHistory(history: AviationAccountReportingStatusHistoryDTO[]) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        reportingStatusHistory: {
          ...state.currentAccount.reportingStatusHistory,
          history,
        },
      },
    });
  }

  setTotal(total: number) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        reportingStatusHistory: {
          ...state.currentAccount.reportingStatusHistory,
          total,
        },
      },
    });
  }

  setPaging(paging: Paging) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        reportingStatusHistory: {
          ...state.currentAccount.reportingStatusHistory,
          paging,
        },
      },
    });
  }

  resetReportingStatusHistory() {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatusHistory = initialCurrentAccountState.reportingStatusHistory;
      }),
    );
  }

  createAccount(): Observable<void> {
    return this.service
      .createAviationAccount(this.getState().createAccount.newAccount)
      .pipe(this.pendingRequestService.trackRequest());
  }

  editAccount(): Observable<HttpResponse<void>> {
    const existingAccount = this.getState().currentAccount.account.aviationAccount;
    const accountToUpdate: {
      name: string;
      registryId: number;
      sopId: number;
      crcoCode: string;
      commencementDate: string;
      location: any;
    } = (({ name, registryId, sopId, crcoCode, commencementDate, location }) => ({
      name,
      registryId: registryId ? +registryId : null,
      sopId: sopId ? +sopId : null,
      crcoCode,
      commencementDate,
      location,
    }))(existingAccount);

    return this.aviationAccountUpdateService
      .updateAviationAccount(this.getState().currentAccount.account.aviationAccount.id, accountToUpdate, 'response')
      .pipe(this.pendingRequestService.trackRequest());
  }

  editReportingStatus(reportingStatus: AviationAccountReportingStatusHistoryCreationDTO): Observable<any> {
    return this.reportingStatusService
      .submitReportingStatus(this.getState().currentAccount.account.aviationAccount.id, reportingStatus)
      .pipe(
        this.pendingRequestService.trackRequest(),
        tap(() => {
          this.setCurrentAccount(
            produce(this.getState().currentAccount.account, (account) => {
              account.aviationAccount.reportingStatus = reportingStatus.status;
              account.aviationAccount.reportingStatusReason = reportingStatus.reason;
            }),
          );
        }),
      );
  }
}
