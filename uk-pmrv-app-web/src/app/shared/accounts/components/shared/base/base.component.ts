/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, map, Observable, switchMap, takeUntil } from 'rxjs';

import { AviationAccountDetails, AviationAccountsStore, selectAccountInfo } from '@aviation/accounts/store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore, selectCurrentDomain } from '@core/store';
import { originalOrder } from '@shared/keyvalue-order';

import { InstallationAccountPermitDTO, RequestDetailsDTO, RequestSearchCriteria, RequestsService } from 'pmrv-api';

export interface Reports {
  year: string;
  reportableEmissions: number;
  reportableOffsetEmissions?: number;
  reportableReductionClaimEmissions?: number;
  details: RequestDetailsDTO[];
  isExempt?: boolean;
  hasEmissionReportNotRequired?: boolean;
  hasDoeCorsia?: boolean;
}

export interface Inspections {
  year: string;
  details: RequestDetailsDTO[];
}

@Component({
  template: '',
})
export abstract class BaseComponent implements OnInit {
  abstract category: RequestSearchCriteria['category'];
  abstract currentPageData$: Observable<Reports[] | Inspections[]>;
  abstract data$: Observable<Reports[] | Inspections[]>;

  domain: AccountType;

  readonly originalOrder = originalOrder;
  readonly itemsPerPage = 5;
  page$ = new BehaviorSubject<number>(1);
  showPagination$ = new BehaviorSubject<boolean>(true);
  totalDataNumber$ = new BehaviorSubject<number>(0);

  searchForm: UntypedFormGroup;

  searchTypes$ = new BehaviorSubject<RequestDetailsDTO['requestType'][]>([]);
  searchStatuses$ = new BehaviorSubject<RequestSearchCriteria['requestStatuses'][]>([]);
  readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);

  accountId$: Observable<number>;

  constructor(
    protected fb: UntypedFormBuilder,
    protected authStore: AuthStore,
    protected route: ActivatedRoute,
    protected destroy$: DestroySubject,
    protected requestsService: RequestsService,
    protected store: AviationAccountsStore,
  ) {
    this.searchForm = this.fb.group({
      types: [[], { updateOn: 'change' }],
      statuses: [[], { updateOn: 'change' }],
    });
  }

  ngOnInit(): void {
    this.currentDomain$.subscribe((domain) => {
      this.domain = domain;
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

    this.searchForm
      .get('types')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchTypes$.next(this.searchForm.get('types').value));

    this.searchForm
      .get('statuses')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchStatuses$.next(this.searchForm.get('statuses').value));
  }

  getRequestDetails() {
    return combineLatest([this.accountId$, this.searchTypes$, this.searchStatuses$]).pipe(
      switchMap(([accountId, types, statuses]) =>
        this.requestsService.getRequestDetailsByResource({
          resourceType: 'ACCOUNT',
          resourceId: String(accountId),
          category: this.category,
          requestTypes: types.reduce((acc, val) => acc.concat(val), []),
          requestStatuses: statuses.reduce((acc, val) => acc.concat(val), []),
          pageNumber: 0,
          pageSize: 999999,
        }),
      ),
    );
  }

  groupDetailsByYear(data: RequestDetailsDTO[]) {
    const detailsByYear = data.reduce((acc, val) => {
      const year = (val.requestMetadata as any).year || ((val.requestMetadata as any).years[0] as number);
      const yearDetails = acc[year] || [];

      return {
        ...acc,
        [year]: [...yearDetails, val],
      };
    }, {});

    return detailsByYear;
  }
}
