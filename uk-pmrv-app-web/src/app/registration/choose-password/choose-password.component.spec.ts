import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, MockType } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { SharedUserModule } from '../../shared-user/shared-user.module';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ChoosePasswordComponent } from './choose-password.component';

describe('ChoosePasswordComponent', () => {
  let component: ChoosePasswordComponent;
  let fixture: ComponentFixture<ChoosePasswordComponent>;
  let page: Page;

  class Page extends BasePage<ChoosePasswordComponent> {
    get emailValue() {
      return this.getInputValue('#email');
    }

    get passwordValue() {
      return this.getInputValue('#password');
    }

    set passwordValue(password: string) {
      this.setInputValue('#password', password);
    }

    get repeatedPasswordValue() {
      return this.query<HTMLInputElement>('#validatePassword').value;
    }

    set repeatedPasswordValue(password: string) {
      this.setInputValue('#validatePassword', password);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const operatorUsersRegistrationService: MockType<OperatorUsersRegistrationService> = {
    acceptAuthorityAndSetCredentialsToUser: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule, ReactiveFormsModule, SharedUserModule],
      declarations: [ChoosePasswordComponent],
      providers: [
        UserRegistrationStore,
        { provide: OperatorUsersRegistrationService, useValue: operatorUsersRegistrationService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChoosePasswordComponent);
    component = fixture.debugElement.componentInstance;
    page = new Page(fixture);
    component.form.controls['password'].clearAsyncValidators();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from store', inject(
    [UserRegistrationStore],
    fakeAsync((store: UserRegistrationStore) => {
      store.setState({ password: 'password', email: 'test@pmrv.uk' });

      tick();
      fixture.detectChanges();

      expect(page.emailValue).toBe('test@pmrv.uk');
      expect(page.passwordValue).toBe('password');
      expect(page.repeatedPasswordValue).toBe('password');
    }),
  ));

  it('should submit only if form valid', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.passwordValue = '';
    page.repeatedPasswordValue = '';
    page.submitButton.click();
    fixture.detectChanges();

    page.passwordValue = 'test';
    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalled();
  }));

  it('should navigate to summary when creating an operator from an emitter', inject(
    [Router, UserRegistrationStore],
    (router: Router, store: UserRegistrationStore) => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
      const token = 'thisisatoken';
      const password = 'ThisIsAStrongP@ssw0rd';

      store.setState({
        invitationStatus: 'ALREADY_REGISTERED_SET_PASSWORD_ONLY',
        token: token,
        password: password,
      });

      page.passwordValue = password;
      page.repeatedPasswordValue = password;

      page.submitButton.click();
      fixture.detectChanges();

      expect(operatorUsersRegistrationService.acceptAuthorityAndSetCredentialsToUser).toHaveBeenCalledWith({
        invitationToken: token,
        password: password,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../success'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    },
  ));
});
