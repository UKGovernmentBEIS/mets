import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PasswordStrengthMeterModule, PasswordStrengthMeterService } from 'angular-password-strength-meter';

import { SharedModule } from '../shared/shared.module';
import { VerifiersTableComponent } from './component/verifiers-table/verifiers-table.component';
import { PasswordComponent } from './password/password.component';
import { PasswordService } from './password/password.service';
import { SubmitIfEmptyPipe } from './pipes/submit-if-empty.pipe';

@NgModule({
  declarations: [PasswordComponent, SubmitIfEmptyPipe, VerifiersTableComponent],
  imports: [PasswordStrengthMeterModule, RouterModule, SharedModule],
  providers: [PasswordService, PasswordStrengthMeterService],
  exports: [PasswordComponent, PasswordStrengthMeterModule, SubmitIfEmptyPipe, VerifiersTableComponent],
})
export class SharedUserModule {}
