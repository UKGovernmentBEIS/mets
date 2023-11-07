import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { GovukComponentsModule } from 'govuk-components';

import { ForgotPasswordService } from 'pmrv-api';

import { SharedUserModule } from '../../shared-user/shared-user.module';
import { ResetPasswordStore } from '../store/reset-password.store';
import { SubmitOtpComponent } from './submit-otp.component';

describe('SubmitOtpComponent', () => {
  let component: SubmitOtpComponent;
  let fixture: ComponentFixture<SubmitOtpComponent>;
  let page: Page;
  let router: Router;
  let resetPasswordStore: ResetPasswordStore;

  const forgotPasswordService = mockClass(ForgotPasswordService);
  const authService = mockClass(AuthService);

  class Page extends BasePage<SubmitOtpComponent> {
    get passwordValue() {
      return this.getInputValue('#otp');
    }
    set passwordValue(value: string) {
      this.setInputValue('#otp', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get link() {
      return this.query<HTMLAnchorElement>('.govuk-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule, ReactiveFormsModule, SharedUserModule],
      declarations: [SubmitOtpComponent],
      providers: [
        KeycloakService,
        { provide: AuthService, useValue: authService },
        { provide: ForgotPasswordService, useValue: forgotPasswordService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(SubmitOtpComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    resetPasswordStore = TestBed.inject(ResetPasswordStore);

    resetPasswordStore.setState({
      ...resetPasswordStore.getState(),
      password: 'password',
      token: 'token',
    });

    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display errors for invalid otp codes', () => {
    expect(page.submitButton.disabled).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Enter the 6-digit code']);

    page.passwordValue = 'abcdef';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain numbers only']);

    page.passwordValue = '123';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain exactly 6 characters']);

    page.passwordValue = '1234567';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain exactly 6 characters']);
  });

  it('should submit the forgot password request', () => {
    forgotPasswordService.resetPassword.mockReturnValueOnce(of({}));

    page.passwordValue = '123456';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(forgotPasswordService.resetPassword).toHaveBeenCalledTimes(1);
    expect(forgotPasswordService.resetPassword).toHaveBeenCalledWith({
      password: 'password',
      token: 'token',
      otp: '123456',
    });
  });

  it('should navigate to 404 if user status is invalid', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    forgotPasswordService.resetPassword.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'USER1005' } })),
    );

    page.passwordValue = '123456';
    page.submitButton.click();
    fixture.detectChanges();

    expect(forgotPasswordService.resetPassword).toHaveBeenCalledTimes(1);
    expect(forgotPasswordService.resetPassword).toHaveBeenCalledWith({
      password: 'password',
      token: 'token',
      otp: '123456',
    });

    expect(navigateSpy).toHaveBeenCalledWith(['error', '404']);
  });

  it('should go to login after clicking link', () => {
    jest.spyOn(fixture.componentInstance, 'onSignInAgain');
    forgotPasswordService.resetPassword.mockReturnValueOnce(of({}));

    page.passwordValue = '123456';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();

    page.link.click();
    fixture.detectChanges();

    expect(component.onSignInAgain).toHaveBeenCalledTimes(1);
    expect(authService.login).toHaveBeenCalledTimes(1);
  });
});
