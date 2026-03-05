import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import cleanDeep from 'clean-deep';

import { OperatorUserRegistrationWithCredentialsDTO, OperatorUsersRegistrationService } from 'pmrv-api';

import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  userInfo$ = this.store.select('userRegistrationDTO');
  invitationStatus$ = this.store.select('invitationStatus');

  isSubmitDisabled: boolean;

  constructor(
    private readonly store: UserRegistrationStore,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  registerUser(): void {
    this.isSubmitDisabled = true;
    const isNoPasswordInvitation =
      this.store.getState().invitationStatus === 'PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD';

    combineLatest([this.store.select('userRegistrationDTO'), this.store.select('password'), this.store.select('token')])
      .pipe(
        first(),
        map(([user, password, emailToken]) => cleanDeep({ ...user, password, emailToken })),
        withLatestFrom(this.store.select('isInvited')),
        switchMap(([user, isInvited]: [OperatorUserRegistrationWithCredentialsDTO, boolean]) =>
          isNoPasswordInvitation
            ? this.operatorUsersRegistrationService.acceptAuthorityAndEnableInvitedUserWithoutCredentials(user)
            : isInvited
              ? this.operatorUsersRegistrationService.acceptAuthorityAndEnableInvitedUserWithCredentials(user)
              : this.operatorUsersRegistrationService.registerUser(user),
        ),
      )
      .subscribe(() =>
        this.router.navigate([isNoPasswordInvitation ? '../../invitation' : '../success'], { relativeTo: this.route }),
      );
  }
}
