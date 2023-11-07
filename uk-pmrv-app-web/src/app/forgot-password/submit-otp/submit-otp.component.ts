import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { BehaviorSubject, combineLatest, EMPTY, first, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { ForgotPasswordService } from 'pmrv-api';

import { ResetPasswordStore } from '../store/reset-password.store';

@Component({
  selector: 'app-submit-otp',
  templateUrl: './submit-otp.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitOtpComponent implements OnInit {
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  email$ = this.store.select('email');
  isPasswordReset = false;

  form = this.fb.group({
    otp: [
      null,
      [
        GovukValidators.required('Enter the 6-digit code'),
        GovukValidators.pattern('[0-9]*', 'Digit code must contain numbers only'),
        GovukValidators.minLength(6, 'Digit code must contain exactly 6 characters'),
        GovukValidators.maxLength(6, 'Digit code must contain exactly 6 characters'),
      ],
    ],
  });

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly forgotPasswordService: ForgotPasswordService,
    private readonly fb: UntypedFormBuilder,
    private readonly backLinkService: BackLinkService,
    private readonly store: ResetPasswordStore,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.store.select('token'), this.store.select('password')])
      .pipe(
        first(),
        switchMap(([token, password]) =>
          this.forgotPasswordService.resetPassword({
            token: token,
            otp: this.form.value.otp,
            password: password,
          }),
        ),
        catchBadRequest([ErrorCodes.OTP1001, ErrorCodes.USER1004, ErrorCodes.USER1005], (res) => {
          switch (res.error.code) {
            case ErrorCodes.OTP1001:
              this.form.get('otp').setErrors({ otpInvalid: 'Invalid OTP' });
              break;
            case ErrorCodes.USER1004:
            case ErrorCodes.USER1005:
              this.router.navigate(['error', '404']);
          }
          this.isSummaryDisplayed$.next(true);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.isPasswordReset = true;
        this.backLinkService.hide();
      });
  }

  onSignInAgain(): void {
    this.authService.login({ redirectUri: location.origin });
  }
}
