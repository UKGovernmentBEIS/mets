import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { Store } from '@core/store';
import { Paging } from '@shared/model';
import produce from 'immer';

import {
  AccountDetailsHistoryDTO,
  AviationAccountCreationDTO,
  AviationAccountEmpDTO,
  AviationAccountReportingObligationFirstYearDTO,
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
  ReportingStatusListItem,
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

  submitReportingStatus(
    year: string,
    reportingStatus: AviationAccountReportingStatusHistoryCreationDTO,
  ): Observable<any> {
    return this.reportingStatusService
      .submitReportingStatus(this.getState().currentAccount.account.aviationAccount.id, {
        ...reportingStatus,
        year: +year,
      })
      .pipe(
        this.pendingRequestService.trackRequest(),
        switchMap(() => {
          const { account, reportingStatus } = this.getState().currentAccount;
          return this.reportingStatusService.getAllReportingStatuses(
            account.aviationAccount.id,
            0,
            reportingStatus?.paging.pageSize,
          );
        }),
        tap((res) => {
          this.setReportingStatuses((res as any)?.reportingStatusList);
          this.setReportingStatusTotal((res as any)?.total);
        }),
      );
  }

  editReportingStatus(reportingStatus: AviationAccountReportingStatusHistoryCreationDTO): void {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.upsertStatus = reportingStatus;
      }),
    );
  }

  setReportingStatuses(reportingStatuses: Array<ReportingStatusListItem>) {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.statuses = reportingStatuses;
      }),
    );
  }

  setReportingStatusTotal(total: number) {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.total = total;
      }),
    );
  }

  setReportingStatusCurrentPage(page: number) {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.paging.page = page;
      }),
    );
  }

  setCurrentStatus(currentStatus: ReportingStatusListItem) {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.currentStatus = currentStatus;
      }),
    );
  }

  resetEditReportingStatus() {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.reportingStatus.upsertStatus = undefined;
        state.currentAccount.reportingStatus.currentStatus = undefined;
      }),
    );
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

  setReportingStatusHistoryTotal(total: number) {
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

  setReportingStatusHistoryPaging(paging: Paging) {
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
      sopId: number;
      crcoCode: string;
      location: any;
    } = (({ name, sopId, crcoCode, location }) => ({
      name,
      sopId: sopId ? +sopId : null,
      crcoCode,
      location,
    }))(existingAccount);

    return this.aviationAccountUpdateService
      .updateAviationAccount(this.getState().currentAccount.account.aviationAccount.id, accountToUpdate, 'response')
      .pipe(this.pendingRequestService.trackRequest());
  }

  resetUpsertFyro() {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.upsertFirstYearOfReportingObligation = undefined;
      }),
    );
  }

  editFirstYearOfReportingObligation(fyro: AviationAccountReportingObligationFirstYearDTO): void {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.upsertFirstYearOfReportingObligation = fyro;
      }),
    );
  }

  submitFirstYearOfReportingObligation(fyro: AviationAccountReportingObligationFirstYearDTO): Observable<any> {
    const accountId = this.getState().currentAccount.account.aviationAccount.id;
    const accountFyroToUpdate = {
      commencementDate: fyro?.commencementDate,
      reason: fyro?.reason,
    };

    return this.aviationAccountUpdateService
      .updateRegistryReportingFirstYear1(accountId, accountFyroToUpdate, 'response')
      .pipe(
        this.pendingRequestService.trackRequest(),
        switchMap(() => {
          const { paging } = this.getState().currentAccount.reportingStatus;
          return this.reportingStatusService.getAllReportingStatuses(accountId, 0, paging.pageSize);
        }),
        tap((res) => {
          this.setReportingStatuses(res?.reportingStatusList);
          this.setReportingStatusTotal(res?.total);
        }),
      );
  }

  setAccountDetailsHistory(history: AccountDetailsHistoryDTO[]) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        accountDetailsHistory: {
          ...state.currentAccount.accountDetailsHistory,
          history,
        },
      },
    });
  }

  setAccountDetailsHistoryTotal(total: number) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        accountDetailsHistory: {
          ...state.currentAccount.accountDetailsHistory,
          total,
        },
      },
    });
  }

  setAccountDetailsHistoryPaging(paging: Paging) {
    const state = this.getState();
    this.setState({
      ...state,
      currentAccount: {
        ...state.currentAccount,
        accountDetailsHistory: {
          ...state.currentAccount.accountDetailsHistory,
          paging,
        },
      },
    });
  }

  resetAccountDetailsHistory() {
    this.setState(
      produce(this.getState(), (state) => {
        state.currentAccount.accountDetailsHistory = initialCurrentAccountState.accountDetailsHistory;
      }),
    );
  }
}
