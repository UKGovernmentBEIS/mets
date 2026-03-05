import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { OperatorUsersService, RegulatorUsersService, VerifierUsersService } from 'pmrv-api';

@Component({
  selector: 'app-reset-two-fa',
  templateUrl: './reset-two-fa.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetTwoFaComponent {
  userId$ = this.route.paramMap.pipe(map(() => window.history.state['userId']));
  accountId$ = this.route.paramMap.pipe(map(() => window.history.state['accountId']));
  userName$ = this.route.paramMap.pipe(map(() => window.history.state['userName']));
  role$ = this.route.paramMap.pipe(map(() => window.history.state['role']));

  constructor(
    readonly location: Location,
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly verifierUsersService: VerifierUsersService,
    private readonly operatorUsersService: OperatorUsersService,
    private readonly route: ActivatedRoute,
  ) {}

  reset() {
    combineLatest([this.userId$, this.accountId$, this.role$])
      .pipe(
        first(),
        switchMap(([userId, accountId, role]) => {
          switch (role) {
            case 'REGULATOR':
              return this.regulatorUsersService.resetRegulator2Fa(userId);
            case 'VERIFIER':
              return this.verifierUsersService.resetVerifier2Fa(userId);
            case 'OPERATOR':
              return this.operatorUsersService.resetOperator2Fa(accountId, userId);
          }
        }),
      )
      .subscribe(() => this.location.back());
  }
}
