import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PasswordStrengthMeterComponent } from 'angular-password-strength-meter';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { SharedModule } from '../shared/shared.module';
import { VerifiersTableComponent } from './component/verifiers-table/verifiers-table.component';
import { PasswordComponent } from './password/password.component';
import { PasswordService } from './password/password.service';
import { SubmitIfEmptyPipe } from './pipes/submit-if-empty.pipe';

@NgModule({
  imports: [PasswordStrengthMeterComponent, RouterModule, SharedModule],
  declarations: [PasswordComponent, SubmitIfEmptyPipe, VerifiersTableComponent],
  providers: [PasswordService, provideZxvbnServiceForPSM()],
  exports: [PasswordComponent, SubmitIfEmptyPipe, VerifiersTableComponent],
})
export class SharedUserModule {}
