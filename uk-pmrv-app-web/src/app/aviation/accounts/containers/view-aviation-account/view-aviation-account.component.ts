import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType, UserState } from '@core/store/auth';

import { AviationAccountDetails, AviationAccountsStore, selectAccountInfo } from '../../store';

interface ViewModel {
  accountInfo: AviationAccountDetails;
  userRoleType: UserState['roleType'];
}

@Component({
  selector: 'app-view-aviation-account',
  templateUrl: './view-aviation-account.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ViewAviationAccountComponent implements OnDestroy {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(selectAccountInfo),
    this.authStore.pipe(selectUserRoleType),
  ]).pipe(
    map(([accountInfo, userRoleType]) => ({
      accountInfo,
      userRoleType,
    })),
  );

  currentTab$ = new BehaviorSubject<string>(null);

  constructor(
    private readonly store: AviationAccountsStore,
    readonly authStore: AuthStore,
    private router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnDestroy(): void {
    this.currentTab$.complete();
  }

  selectedTab(selected: string) {
    this.router.navigate([], {
      relativeTo: this.route,
      preserveFragment: true,
    });
    this.currentTab$.next(selected);
  }
}
