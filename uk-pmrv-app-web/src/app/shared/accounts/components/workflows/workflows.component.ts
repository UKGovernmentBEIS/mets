import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  shareReplay,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore, selectCurrentDomain } from '@core/store/auth';

import {
  InstallationAccountPermitDTO,
  RequestDetailsDTO,
  RequestDetailsSearchResults,
  RequestSearchByAccountCriteria,
  RequestsService,
} from 'pmrv-api';

import { AviationAccountDetails, AviationAccountsStore, selectAccountInfo } from '../../../../aviation/accounts/store';
import { statusesTagMap } from '../../../../workflow-item/shared/statusesTagMap';
import { workflowStatusesMap } from '../../../../workflow-item/shared/workflowStatusesMap';
import { originalOrder } from '../../../keyvalue-order';
import { workflowTypesDomainMap } from './workflowTypesMap';

@Component({
  selector: 'app-workflows',
  templateUrl: './workflows.component.html',
  styles: [
    `
      span.search-results-list_item_status {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class WorkflowsComponent implements OnInit {
  @Input() currentTab: string;

  readonly originalOrder = originalOrder;
  readonly workflowTypesDomainMap = workflowTypesDomainMap;
  readonly workflowStatusesMap = workflowStatusesMap;
  readonly workflowStatusesTagMap = statusesTagMap;

  readonly pageSize = 30;
  page$ = new BehaviorSubject<number>(1);

  accountId$: Observable<number>;
  workflowResults$: Observable<RequestDetailsSearchResults>;
  showPagination$ = new BehaviorSubject<boolean>(true);
  domain: AccountType;
  workflowTypesPerDomain: Record<string, string[]>;
  headingTitle: string;

  searchForm: UntypedFormGroup = this.fb.group({
    workflowTypes: [[], { updateOn: 'change' }],
    workflowStatuses: [[], { updateOn: 'change' }],
  });

  private searchTypes$ = new BehaviorSubject<RequestDetailsDTO['requestType'][]>([]);
  private searchStatuses$ = new BehaviorSubject<RequestSearchByAccountCriteria['requestStatuses'][]>([]);
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    private readonly requestsService: RequestsService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly store: AviationAccountsStore,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain;
      this.headingTitle = this.domain === 'INSTALLATION' ? 'Permit history' : 'Emissions monitoring plan history';
    });

    if (this.domain === 'INSTALLATION') {
      this.accountId$ = (
        this.route.data as Observable<{
          accountPermit: InstallationAccountPermitDTO;
        }>
      ).pipe(map((data) => data.accountPermit?.account.id));
    } else {
      this.accountId$ = this.store.pipe(selectAccountInfo).pipe(
        filter((account) => !!account),
        map((account: AviationAccountDetails) => account.id),
      );
    }

    this.workflowTypesPerDomain = workflowTypesDomainMap[this.domain];
    this.searchForm
      .get('workflowTypes')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchTypes$.next(this.searchForm.get('workflowTypes').value));

    this.searchForm
      .get('workflowStatuses')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchStatuses$.next(this.searchForm.get('workflowStatuses').value));

    this.workflowResults$ = combineLatest([
      this.accountId$,
      this.searchTypes$,
      this.searchStatuses$,
      this.page$.pipe(distinctUntilChanged()),
    ]).pipe(
      switchMap(([accountId, types, statuses, page]) =>
        this.requestsService.getRequestDetailsByAccountId({
          accountId: accountId,
          category: 'PERMIT',
          requestTypes: types.reduce((acc, val) => acc.concat(val), []),
          requestStatuses: statuses.reduce((acc, val) => acc.concat(val), []),
          pageNumber: page - 1,
          pageSize: this.pageSize,
        }),
      ),
      tap((workflows) => {
        this.showPagination$.next(workflows.total > this.pageSize);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  workflowName(requestType: RequestDetailsDTO['requestType']): string {
    return Object.entries(this.workflowTypesPerDomain).find((e) => e[1].includes(requestType))?.[0];
  }
}
