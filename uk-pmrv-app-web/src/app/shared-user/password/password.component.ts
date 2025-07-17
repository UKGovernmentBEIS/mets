import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-password',
  templateUrl: './password.component.html',
  styleUrl: './password.component.scss',
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class PasswordComponent {
  @Input() passwordLabel = 'Create a password to activate your account';
  @Input() confirmPasswordLabel = 'Re-enter your password';
  showLabel: 'Show' | 'Hide' = 'Show';
  passwordInputType: 'password' | 'text' = 'password';
  passwordStrength: number;

  constructor(readonly formGroupDirective: FormGroupDirective) {}

  togglePassword() {
    if (this.showLabel === 'Show') {
      this.showLabel = 'Hide';
      this.passwordInputType = 'text';
    } else {
      this.showLabel = 'Show';
      this.passwordInputType = 'password';
    }
  }
}
