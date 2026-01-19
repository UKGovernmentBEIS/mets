import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, switchMap, tap } from 'rxjs';

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

  editAccountCommencementDate(): Observable<HttpResponse<void>> {
    const existingAccount = this.getState().currentAccount.account.aviationAccount;
    const accountToUpdate: {
      commencementDate: string;
    } = (({ commencementDate }) => ({ commencementDate }))(existingAccount);

    return this.aviationAccountUpdateService
      .updateCommencementDate1(this.getState().currentAccount.account.aviationAccount.id, accountToUpdate, 'response')
      .pipe(this.pendingRequestService.trackRequest());
  }
}
