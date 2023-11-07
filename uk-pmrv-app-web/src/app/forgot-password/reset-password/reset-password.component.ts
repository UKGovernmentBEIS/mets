import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, of, take } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { ForgotPasswordService } from 'pmrv-api';

import { PASSWORD_FORM, passwordFormFactory } from '../../shared-user/password/password-form.factory';
import { ResetPasswordStore } from '../store/reset-password.store';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordComponent implements OnInit {
  isSummaryDisplayed = false;
  passwordLabel = 'New password';
  newPasswordLabel = 'Confirm new password';
  token: string;
  email: string;

  constructor(
    @Inject(PASSWORD_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: ResetPasswordStore,
    private readonly forgotPasswordService: ForgotPasswordService,
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    this.forgotPasswordService
      .verifyToken({ token: this.token })
      .pipe(
        map((emailDTO) => {
          this.store.setState({ ...this.store.getState(), email: emailDTO.email });
        }),
        map(() => ({ url: 'success' })),
        catchBadRequest([ErrorCodes.EMAIL1001, ErrorCodes.TOKEN1001], (res) =>
          of({ url: 'invalid-link', code: res.error.code }),
        ),
      )
      .subscribe(({ code, url }: { url: string; code: string }) => {
        if (url !== 'success') {
          code === ErrorCodes.TOKEN1001
            ? this.router.navigate(['error', '404'])
            : this.router.navigate(['forgot-password', url]);
        }
      });

    this.store
      .select('password')
      .pipe(
        map((password) => this.form.patchValue({ password, validatePassword: password })),
        take(1),
      )
      .subscribe();
  }

  submitPassword(): void {
    if (this.form.valid) {
      this.store.setState({ ...this.store.getState(), password: this.form.get('password').value, token: this.token });

      this.router.navigate(['../otp'], {
        relativeTo: this.route,
      });
    } else {
      this.isSummaryDisplayed = true;
    }
  }
}
