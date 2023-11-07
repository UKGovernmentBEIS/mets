import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { SharedUserModule } from '../shared-user/shared-user.module';
import { EmailLinkInvalidComponent } from './email-link-invalid/email-link-invalid.component';
import { EmailSentComponent } from './email-sent/email-sent.component';
import { ForgotPasswordRoutingModule } from './forgot-password-routing.module';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { SubmitEmailComponent } from './submit-email/submit-email.component';
import { SubmitOtpComponent } from './submit-otp/submit-otp.component';

@NgModule({
  declarations: [
    EmailLinkInvalidComponent,
    EmailSentComponent,
    ResetPasswordComponent,
    SubmitEmailComponent,
    SubmitOtpComponent,
  ],
  imports: [CommonModule, ForgotPasswordRoutingModule, SharedModule, SharedUserModule],
})
export class ForgotPasswordModule {}
