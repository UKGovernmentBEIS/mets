import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';

import { InstallationAccountDTO, InstallationAccountPermitDTO } from 'pmrv-api';

import { accountFinalStatuses } from '../core/accountFinalStatuses';

@Component({
  selector: 'app-account-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DetailsComponent {
  @Input() currentTab: string;

  accountPermit$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((data) => data?.accountPermit));

  permit$ = this.accountPermit$.pipe(map((accountPermit) => accountPermit.permit));
  account$ = this.accountPermit$.pipe(map((accountPermit) => accountPermit.account as InstallationAccountDTO));

  userRoleType$ = this.authStore.pipe(selectUserRoleType, takeUntil(this.destroy$));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  canChangeByStatus(status: InstallationAccountDTO['status']): boolean {
    return accountFinalStatuses(status);
  }
}
