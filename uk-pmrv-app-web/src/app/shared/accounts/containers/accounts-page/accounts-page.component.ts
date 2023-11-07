import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, distinctUntilChanged, map, Observable, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectUserRoleType, UserState } from '@core/store/auth';
import { AuthStore } from '@core/store/auth/auth.store';
import {
  AccountSearchResult,
  AccountsService,
  AccountsStore,
  initialState,
  selectAccounts,
  selectPage,
  selectPageSize,
  selectSearchErrorSummaryVisible,
  selectSearchTerm,
  selectTotal,
} from '@shared/accounts';

import { GovukValidators } from 'govuk-components';

interface ViewModel {
  userRoleType: UserState['roleType'];
  searchTerm: string;
  accounts: AccountSearchResult[];
  total: number;
  page: number;
  pageSize: number;
  isSummaryDisplayed: boolean;
}

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AccountsPageComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.authStore.pipe(selectUserRoleType),
    this.store.pipe(selectSearchTerm),
    this.store.pipe(selectAccounts),
    this.store.pipe(selectTotal),
    this.store.pipe(selectPage),
    this.store.pipe(selectPageSize),
    this.store.pipe(selectSearchErrorSummaryVisible),
  ]).pipe(
    map(([role, searchTerm, accounts, total, page, pageSize, searchErrorSummaryVisible]) => ({
      userRoleType: role,
      searchTerm,
      accounts,
      total,
      page,
      pageSize,
      isSummaryDisplayed: searchErrorSummaryVisible,
    })),
  );

  searchForm: UntypedFormGroup = this.fb.group({
    term: [
      null,
      {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(256, 'Enter up to 256 characters'),
        ],
      },
    ],
  });

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    private readonly authStore: AuthStore,
    private readonly store: AccountsStore,
    private readonly accountsService: AccountsService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.vm$
      .pipe(
        map(({ searchTerm, page, pageSize }) => ({ searchTerm, page, pageSize })),
        distinctUntilChanged((previous, current) => {
          return (
            previous.page === current.page &&
            previous.pageSize === current.pageSize &&
            previous.searchTerm === current.searchTerm
          );
        }),
        switchMap(({ searchTerm, page, pageSize }) => {
          return this.accountsService.getCurrentUserAccounts(page - 1, pageSize, searchTerm);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe(({ accounts, total }) => {
        this.store.setAccounts(accounts);
        this.store.setTotal(total);
      });

    this.route.queryParamMap
      .pipe(
        map((params) => ({
          term: params.get('term')?.trim() || initialState.searchTerm,
          page: +params.get('page') || initialState.paging.page,
          pageSize: +params.get('pageSize') || initialState.paging.pageSize,
        })),
        takeUntil(this.destroy$),
      )
      .subscribe(({ term, page, pageSize }) => {
        this.termCtrl.setValue(term);

        this.store.setPaging({ page, pageSize });
        if (this.searchForm.valid) {
          this.store.setSearchTerm(term);
        }
      });
  }

  onPageChange(page: number) {
    this.router.navigate([], {
      queryParams: { page },
      queryParamsHandling: 'merge',
      relativeTo: this.route,
    });
  }

  onSearch() {
    if (this.searchForm.valid) {
      this.store.setSearchErrorSummaryVisible(false);
      this.router.navigate([], {
        queryParams: {
          term: this.termCtrl.value || null,
          page: null,
        },
        queryParamsHandling: 'merge',
        relativeTo: this.route,
      });
    } else {
      this.store.setSearchErrorSummaryVisible(true);
    }
  }

  navigateToAccount(accountId: number): void {
    this.router.navigate([accountId], {
      relativeTo: this.route,
      state: { accountSearchParams: this.route.snapshot.queryParams },
    });
  }

  private get termCtrl(): AbstractControl {
    return this.searchForm && this.searchForm.get('term');
  }
}
