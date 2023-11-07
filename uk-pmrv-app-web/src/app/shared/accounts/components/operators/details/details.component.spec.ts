import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { OperatorUsersService } from 'pmrv-api';

import { operator, operatorUserRole } from '../../../../../accounts/testing/mock-data';
import { TwoFaLinkComponent } from '../../../../two-fa-link/two-fa-link.component';
import { saveNotFoundOperatorError } from '../errors/business-error';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let operatorUsersService: Partial<jest.Mocked<OperatorUsersService>>;
  let authStore: AuthStore;
  let router: Router;
  let activatedRoute: ActivatedRouteStub;

  class Page extends BasePage<DetailsComponent> {
    get firstNameValue() {
      return this.getInputValue('#firstName');
    }

    set firstNameValue(value: string) {
      this.setInputValue('#firstName', value);
    }

    get lastNameValue() {
      return this.getInputValue('#lastName');
    }

    get phoneNumberValue() {
      return this.getInputValue('#phoneNumber');
    }

    get mobileNumberValue() {
      return this.getInputValue('#mobileNumber');
    }

    get emailValue() {
      return this.getInputValue('#email');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get saveButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get links() {
      return this.queryAll<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    operatorUsersService = {
      updateCurrentOperatorUser: jest.fn().mockReturnValue(of(operator)),
      updateOperatorUserById: jest.fn().mockReturnValue(of(operator)),
    };
    activatedRoute = new ActivatedRouteStub({ accountId: '1', userId: operatorUserRole.userId }, null, {
      user: operator,
    });
    await TestBed.configureTestingModule({
      imports: [BusinessTestingModule, RouterTestingModule, SharedModule],
      declarations: [DetailsComponent, TwoFaLinkComponent],
      providers: [
        { provide: OperatorUsersService, useValue: operatorUsersService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState(operatorUserRole);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pre-populated form', () => {
    expect(page.firstNameValue).toEqual(operator.firstName);
    expect(page.lastNameValue).toEqual(operator.lastName);
    expect(page.phoneNumberValue).toEqual(operator.phoneNumber.number);
    expect(page.mobileNumberValue).toEqual(operator.mobileNumber.number);
    expect(page.emailValue).toEqual(operator.email);
  });

  it('should save the current user', () => {
    authStore.setUserState({ ...operatorUserRole, userId: 'asdf4' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(operatorUsersService.updateCurrentOperatorUser).toHaveBeenCalledWith(operator);
    expect(operatorUsersService.updateOperatorUserById).not.toHaveBeenCalled();
  });

  it('should save other user than the current', () => {
    authStore.setUserState({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();

    expect(operatorUsersService.updateOperatorUserById).toHaveBeenCalledWith(1, operatorUserRole.userId, operator);
    expect(operatorUsersService.updateCurrentOperatorUser).not.toHaveBeenCalled();
  });

  it('should throw an error when updating a deleted user', async () => {
    operatorUsersService.updateOperatorUserById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1004' } })),
    );

    authStore.setUserState({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    await expectBusinessErrorToBe(saveNotFoundOperatorError(1));
  });

  it('should display errors', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    authStore.setUserState({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = '';
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    page.saveButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(operatorUsersService.updateOperatorUserById).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should display the 2fa link for same user', () => {
    expect(page.links.map((el) => el.textContent.trim())).toContain('Change two factor authentication');
  });

  it('should not display the 2fa link for other user', async () => {
    authStore.setUserState({ ...operatorUserRole, userId: 'asdf4' });
    activatedRoute.setParamMap({ userId: '222' });

    await expect(firstValueFrom(component.isLoggedUser$)).resolves.toEqual(false);
  });
});
