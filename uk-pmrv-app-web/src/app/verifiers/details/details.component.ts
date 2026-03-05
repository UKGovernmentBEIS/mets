import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  EMPTY,
  filter,
  first,
  map,
  Observable,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserId, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import cleanDeep from 'clean-deep';

import { GovukValidators } from 'govuk-components';

import {
  CaExternalContactDTO,
  VerifierUserInvitationDTO,
  VerifierUsersInvitationService,
  VerifierUsersService,
} from 'pmrv-api';

import { saveNotFoundVerifierError } from '../errors/business-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DetailsComponent implements OnInit {
  confirmedAddedVerifier$ = new BehaviorSubject<string>(null);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  userFullName: string;

  userId$ = this.route.paramMap.pipe(map((parameters) => parameters.get('userId')));
  isLoggedUser$ = combineLatest([this.authStore.pipe(selectUserId), this.userId$]).pipe(
    first(),
    map(([loggedInUserId, userId]) => loggedInUserId === userId),
  );

  form = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required(`Enter user's first name`),
        GovukValidators.maxLength(255, `The verifier's first name should not be more than 255 characters`),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required(`Enter user's last name`),
        GovukValidators.maxLength(255, `The verifier's last name should not be more than 255 characters`),
      ],
    ],
    phoneNumber: [
      null,
      [
        GovukValidators.required(`Enter user's telephone number`),
        GovukValidators.maxLength(255, 'Telephone number should not be more than 255 characters'),
      ],
    ],
    mobileNumber: [null, GovukValidators.maxLength(255, 'Mobile number should not be more than 255 characters')],
    email: [
      null,
      [
        GovukValidators.required(`Enter user's email address`),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      ],
    ],
  });

  userLoaded$: Observable<CaExternalContactDTO> = this.route.data.pipe(
    map((data) => data?.user),
    filter((user) => user),
    tap((user) => {
      this.form.patchValue(user);
      this.userFullName = user.firstName + ' ' + user.lastName;
    }),
    tap(() => this.form.get('email').disable()),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly verifierUsersInvitationService: VerifierUsersInvitationService,
    private readonly verifierUsersService: VerifierUsersService,
    private readonly authStore: AuthStore,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
    private readonly location: Location,
  ) {}

  ngOnInit(): void {
    this.confirmedAddedVerifier$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.backLinkService.hide();
      } else {
        this.backLinkService.show();
      }
    });
  }

  addNewUser(): void {
    if (this.form.valid) {
      this.route.paramMap
        .pipe(
          first(),
          map((paramMap) => paramMap.get('userId')),
          withLatestFrom(this.route.queryParamMap, this.authStore.pipe(selectUserState)),
          switchMap(([userId, queryParamMap, userState]) =>
            userId
              ? userState.userId === userId
                ? this.verifierUsersService.updateCurrentVerifierUser(this.form.getRawValue())
                : this.verifierUsersService.updateVerifierUserById(userId, this.form.getRawValue())
              : this.verifierUsersInvitationService.inviteVerifierUser(
                  cleanDeep({
                    ...this.form.value,
                    roleCode: queryParamMap.get('roleCode'),
                  }) as VerifierUserInvitationDTO,
                ),
          ),
          catchBadRequest(ErrorCodes.AUTHORITY1006, () =>
            this.businessErrorService.showError(saveNotFoundVerifierError),
          ),
          catchBadRequest([ErrorCodes.USER1001, ErrorCodes.AUTHORITY1005, ErrorCodes.AUTHORITY1015], () => {
            this.form.get('email').setErrors({ existingUser: 'This user email already exists in the service' });
            this.isSummaryDisplayed$.next(true);

            return EMPTY;
          }),
        )
        .subscribe((user) =>
          user ? this.location.back() : this.confirmedAddedVerifier$.next(this.form.get('email').value),
        );
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
