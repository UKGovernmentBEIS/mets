import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { ForgotPasswordService } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-submit-email',
  templateUrl: './submit-email.component.html',
})
export class SubmitEmailComponent implements OnInit {
  isSummaryDisplayed: boolean;
  isEmailSent: boolean;

  form = this.fb.group({
    email: [
      null,
      [
        GovukValidators.required('Enter your email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Enter an email address with a maximum of 255 characters'),
      ],
    ],
  });

  constructor(
    private readonly forgotPasswordService: ForgotPasswordService,
    private readonly fb: UntypedFormBuilder,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.forgotPasswordService.sendResetPasswordEmail({ email: this.form.get('email').value }).subscribe(() => {
        this.isEmailSent = true;
        this.backLinkService.hide();
      });
    } else {
      this.isSummaryDisplayed = true;
    }
  }

  retryResetPassword() {
    this.isEmailSent = false;
  }
}
