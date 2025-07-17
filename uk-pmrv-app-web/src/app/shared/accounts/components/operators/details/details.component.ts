import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, shareReplay, switchMap, tap } from 'rxjs';

import { AuthStore, selectCurrentDomain, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { PhoneInputComponent } from '@shared/phone-input/phone-input.component';

import { GovukValidators } from 'govuk-components';

import { OperatorUserDTO, OperatorUsersService } from 'pmrv-api';

import { saveNotFoundOperatorError } from '../errors/business-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements OnInit {
  userFullName: string;

  userId$ = this.activatedRoute.paramMap.pipe(map((parameters) => parameters.get('userId')));
  accountId$ = this.activatedRoute.paramMap.pipe(map((parameters) => Number(parameters.get('accountId'))));
  isLoggedUser$ = combineLatest([this.authStore.pipe(selectUserState), this.activatedRoute.paramMap]).pipe(
    first(),
    map(([userState, parameters]) => userState.userId === parameters.get('userId')),
  );
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  form$: Observable<UntypedFormGroup> = this.activatedRoute.data.pipe(
    map(({ user }: { user: OperatorUserDTO }) => {
      this.userFullName = user.firstName + ' ' + user.lastName;

      return this.fb.group({
        firstName: [
          user.firstName,
          [
            GovukValidators.required('Enter your first name'),
            GovukValidators.maxLength(255, 'Your first name should not be larger than 255 characters'),
          ],
        ],
        lastName: [
          user.lastName,
          [
            GovukValidators.required('Enter your last name'),
            GovukValidators.maxLength(255, 'Your last name should not be larger than 255 characters'),
          ],
        ],
        phoneNumber: [
          user.phoneNumber,
          [GovukValidators.empty('Enter your phone number'), ...PhoneInputComponent.validators],
        ],
        mobileNumber: [user.mobileNumber, PhoneInputComponent.validators],
        email: [{ value: user.email, disabled: true }],
      });
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly activatedRoute: ActivatedRoute,
    private readonly operatorService: OperatorUsersService,
    private readonly authStore: AuthStore,
    private readonly router: Router,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.form$, this.authStore.pipe(selectUserState), this.activatedRoute.paramMap, this.accountId$])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.isSummaryDisplayed.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, { userId }, params, accountId]) => {
          const payload = {
            ...form.value,
            email: form.get('email').value,
          };
          return userId === params.get('userId')
            ? this.operatorService.updateCurrentOperatorUser(payload)
            : this.operatorService.updateOperatorUserById(accountId, params.get('userId'), payload);
        }),
        catchBadRequest(ErrorCodes.AUTHORITY1004, () =>
          this.accountId$.pipe(
            switchMap((accountId) => this.businessErrorService.showError(saveNotFoundOperatorError(accountId))),
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { fragment: 'users', relativeTo: this.activatedRoute }));
  }
}
