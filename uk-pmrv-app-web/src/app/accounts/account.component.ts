import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable, takeUntil } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { UserState } from '@core/store/auth';
import { selectUserRoleType } from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { InstallationAccountDTO, InstallationAccountPermitDTO } from 'pmrv-api';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styles: `
    span.status {
      margin-left: 30px;
    }
    button.start-new-process {
      float: right;
    }
    #waste-monitoring-notice {
      max-width: 100%;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AccountComponent implements OnInit {
  private accountPermit$: Observable<InstallationAccountPermitDTO>;

  accountDetails$: Observable<InstallationAccountDTO>;
  userRoleType$: Observable<UserState['roleType']>;
  currentTab$ = new BehaviorSubject<string>(null);
  inspectionsWfAccountsTabEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('inspectionsWfAccountsTabEnabled'));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    readonly location: Location,
    readonly store: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly configStore: ConfigStore,
  ) {}

  ngOnInit(): void {
    this.backLinkService.hide();

    this.accountPermit$ = (
      this.route.data as Observable<{
        accountPermit: InstallationAccountPermitDTO;
      }>
    ).pipe(
      takeUntil(this.destroy$),
      map((data) => data?.accountPermit),
    );

    this.accountDetails$ = this.accountPermit$.pipe(map((ap) => ap.account));
    this.userRoleType$ = this.store.pipe(selectUserRoleType);
  }

  selectedTab(selected: string) {
    // upon pagination queryParams is shown, for example "?page=3". In order to avoid any bug from navigation to tabs, clear query params.
    this.router.navigate([], {
      relativeTo: this.route,
      preserveFragment: true,
    });
    this.currentTab$.next(selected);
  }
}
