import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { TwoFaLinkComponent } from '@shared/two-fa-link/two-fa-link.component';
import { ActivatedRouteStub, asyncData, changeInputValue } from '@testing';

import { VerifierUsersInvitationService, VerifierUsersService } from 'pmrv-api';

import { SubmitIfEmptyPipe } from '../../shared-user/pipes/submit-if-empty.pipe';
import { mockVerifier } from '../testing/mock-data';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let location: Location;
  let authStore: AuthStore;

  const verifierUsersInvitationService: Partial<jest.Mocked<VerifierUsersInvitationService>> = {
    inviteVerifierUser: jest.fn().mockReturnValue(of(null)),
  };

  const verifierUsersService: Partial<jest.Mocked<VerifierUsersService>> = {
    updateCurrentVerifierUser: jest.fn().mockReturnValue(asyncData(mockVerifier)),
    updateVerifierUserById: jest.fn().mockReturnValue(asyncData(mockVerifier)),
  };

  const activatedRouteStub = new ActivatedRouteStub(null, { roleCode: 'verifier', userId: '2' });
  const submitButton = () => fixture.nativeElement.querySelector('button[type="submit"]');
  const errorSummary = () => fixture.nativeElement.querySelector('govuk-error-summary');
  const twoFaLink = () =>
    Array.from(fixture.nativeElement.querySelectorAll('a')).map((element) =>
      (element as HTMLElement).textContent.trim(),
    );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent, SubmitIfEmptyPipe, TwoFaLinkComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: VerifierUsersInvitationService, useValue: verifierUsersInvitationService },
        { provide: VerifierUsersService, useValue: verifierUsersService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'VERIFIER',
      userId: '2',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    location = TestBed.inject(Location);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not disable the save button on add verifier', () => {
    expect(submitButton().disabled).toBeFalsy();
  });

  it('should not post form if invalid', () => {
    const inviteVerifierUserSpy = jest.spyOn(verifierUsersInvitationService, 'inviteVerifierUser');

    changeInputValue(fixture, '#firstName', 'Test name');
    changeInputValue(fixture, '#email', 'test');
    changeInputValue(fixture, '#lastName', 'Test');
    changeInputValue(fixture, '#phoneNumber', '1111');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(inviteVerifierUserSpy).not.toHaveBeenCalled();
    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Enter an email address in the correct format, like name@example.com');

    changeInputValue(fixture, '#email', 'test@test.com');
    fixture.detectChanges();

    submitButton().click();

    expect(inviteVerifierUserSpy).toHaveBeenCalledWith({
      email: 'test@test.com',
      firstName: 'Test name',
      lastName: 'Test',
      phoneNumber: '1111',
      roleCode: 'verifier',
    });
  });

  it('should show confirmation when saving', () => {
    changeInputValue(fixture, '#firstName', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#lastName', 'Test');
    changeInputValue(fixture, '#phoneNumber', '1111');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    const panel = (fixture.nativeElement as HTMLElement).querySelector<HTMLDivElement>('.govuk-panel');

    expect(panel).toBeTruthy();
    expect(panel.textContent).toEqual('An account confirmation email has been sent to test@test.com');
  });

  it('should show error summary if email already exists', () => {
    jest
      .spyOn(verifierUsersInvitationService, 'inviteVerifierUser')
      .mockReturnValue(throwError(() => new HttpErrorResponse({ error: { code: 'USER1001' }, status: 400 })));

    changeInputValue(fixture, '#firstName', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#lastName', 'Test');
    changeInputValue(fixture, '#phoneNumber', '1111');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('This user email already exists in the service');
  });

  it('should not show error summary on other backend errors', () => {
    jest
      .spyOn(verifierUsersInvitationService, 'inviteVerifierUser')
      .mockReturnValue(throwError(() => new HttpErrorResponse({ error: { code: 'ANYTHING' } })));

    changeInputValue(fixture, '#firstName', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#lastName', 'Test');
    changeInputValue(fixture, '#phoneNumber', '1111');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(errorSummary()).toBeFalsy();
  });

  describe('Edit verifier', () => {
    beforeEach(() => {
      activatedRouteStub.setParamMap({ userId: '1reg' });
      activatedRouteStub.setResolveMap({ user: mockVerifier });
      fixture.detectChanges();
    });

    it('should change title and button if route contains data', () => {
      expect(fixture.nativeElement.querySelector('h1').textContent).toEqual('User details');
      expect(submitButton().textContent.trim()).toEqual('Save');
    });

    it('should patch changed contact', () => {
      const locationSpy = jest.spyOn(location, 'back');
      const patchUpdateVerifierUserByIdSpy = jest.spyOn(verifierUsersService, 'updateVerifierUserById');

      changeInputValue(fixture, '#firstName', 'New name');
      fixture.detectChanges();

      submitButton().click();
      fixture.detectChanges();

      expect(patchUpdateVerifierUserByIdSpy).toHaveBeenCalledTimes(1);
      expect(patchUpdateVerifierUserByIdSpy).toHaveBeenCalledWith('1reg', {
        email: 'tyrion@got.com',
        firstName: 'New name',
        lastName: 'Lanister',
        mobileNumber: null,
        phoneNumber: '12312321',
      });

      expect(locationSpy).toHaveBeenCalled();
    });

    it('should patch changed contact for current user', () => {
      const locationSpy = jest.spyOn(location, 'back');
      const patchUpdateVerifierUserByIdSpy = jest.spyOn(verifierUsersService, 'updateCurrentVerifierUser');

      activatedRouteStub.setParamMap({ userId: '2' });
      fixture.detectChanges();

      changeInputValue(fixture, '#firstName', 'New name');
      fixture.detectChanges();

      submitButton().click();
      fixture.detectChanges();

      expect(patchUpdateVerifierUserByIdSpy).toHaveBeenCalledTimes(1);
      expect(patchUpdateVerifierUserByIdSpy).toHaveBeenCalledWith({
        email: 'tyrion@got.com',
        firstName: 'New name',
        lastName: 'Lanister',
        mobileNumber: null,
        phoneNumber: '12312321',
      });
      expect(locationSpy).toHaveBeenCalled();
    });

    it('should display the change 2fa link for current user', () => {
      expect(twoFaLink()).toContain('Change two factor authentication');
    });

    it('should display the reset 2fa link for other user', () => {
      activatedRouteStub.setParamMap({ userId: '222' });
      expect(twoFaLink()).toContain('Reset two-factor authentication');
    });
  });
});
