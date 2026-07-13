import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BasePage, changeInputValue } from '@testing';
import { PasswordStrengthMeterComponent } from 'angular-password-strength-meter';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { GovukComponentsModule } from 'govuk-components';

import { ValidatePasswordService } from 'pmrv-api';

import { SharedUserModule } from '../shared-user.module';
import { PasswordComponent } from './password.component';
import { PasswordService } from './password.service';
import { PASSWORD_FORM, passwordFormFactory } from './password-form.factory';

describe('PasswordComponent', () => {
  let component: PasswordComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let validatePasswordService: ValidatePasswordService;

  @Component({
    standalone: false,
    template: `
      <form [formGroup]="form">
        <app-password></app-password>
        <button type="submit">Submit</button>
      </form>
    `,
    providers: [passwordFormFactory],
  })
  class TestComponent {
    constructor(@Inject(PASSWORD_FORM) readonly form: FormGroup) {}
  }

  class Page extends BasePage<TestComponent> {
    get password() {
      return this.query<HTMLInputElement>('#password');
    }

    set passwordValue(value: string) {
      changeInputValue(this.fixture, '#password', value);
    }

    set repeatedPasswordValue(value: string) {
      changeInputValue(this.fixture, '#validatePassword', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get inputErrors() {
      return this.queryAll('.govuk-error-message .govuk-\\!-display-block').map((anchor) => anchor.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        ReactiveFormsModule,
        GovukComponentsModule,
        PasswordStrengthMeterComponent,
        RouterTestingModule,
        SharedUserModule,
      ],
      declarations: [PasswordComponent, TestComponent],
      providers: [PasswordService, ValidatePasswordService, provideZxvbnServiceForPSM()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(PasswordComponent)).componentInstance;
    page = new Page(fixture);
    validatePasswordService = TestBed.inject(ValidatePasswordService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should require the password', () => {
    page.passwordValue = '';
    page.repeatedPasswordValue = '';

    expect(page.inputErrors).toEqual([]);
    expect(component.formGroupDirective.form.get('password').errors.required).toBeTruthy();
    expect(component.formGroupDirective.form.get('password').errors.required).toEqual('Please enter your password');

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.inputErrors).toEqual([
      'Error: Please enter your password',
      'Error: Enter a strong password',
      'Error: Re-enter your password',
    ]);
  });

  it('should not accept weak password', () => {
    page.passwordValue = '12345678';
    page.repeatedPasswordValue = '12345678';
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.get('password').errors.weakPassword).toEqual('Enter a strong password');
    expect(page.inputErrors).toEqual(['Error: Enter a strong password']);
  });

  it('should require the passwords to match', () => {
    page.passwordValue = '12345678';
    page.repeatedPasswordValue = '123456789';
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.errors.notEquivalent).toEqual(
      'Password and re-typed password do not match. Please enter both passwords again',
    );
    expect(page.inputErrors).toEqual(['Error: Enter a strong password']);
  });

  it('should not accept a blacklisted password', async () => {
    const validatePasswordSpy = jest.spyOn(validatePasswordService, 'validatePassword').mockReturnValue(
      of({
        valid: false,
        errors: [
          {
            code: 'PWNED',
            message: 'Password has been blacklisted. Select another password.',
          },
          {
            code: 'BLACKLISTED_PATTERN',
            message: 'Password contains a commonly used or prohibited word or pattern.',
          },
        ],
      }) as any,
    );

    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';
    page.submitButton.click();
    fixture.detectChanges();

    expect(validatePasswordSpy).toHaveBeenCalledWith({ password: 'ThisIsAStrongP@ssw0rd' });
    expect(page.inputErrors).toEqual([
      'Error: Enter a password that does not contain words related to the service or your role',
      'Error: Password has been blacklisted. Select another password',
    ]);
  });
});
