import { APP_BASE_HREF } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import {
  mockAuthorityService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from '@core/guards/mocks';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { ErrorCodes } from '@error/business-errors';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, changeInputValue, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';
import { cloneDeep } from 'lodash-es';

import {
  AuthoritiesService,
  TermsAndConditionsService,
  UsersService,
  VBSiteContactsService,
  VerifierAuthoritiesService,
} from 'pmrv-api';

import { SharedUserModule } from '../shared-user/shared-user.module';
import { savePartiallyNotFoundSiteContactsError, savePartiallyNotFoundVerifierError } from './errors/business-error';
import { SiteContactsComponent } from './site-contacts/site-contacts.component';
import { mockAccountContactVbInfoResponse, mockVerifiersRouteData } from './testing/mock-data';
import { VerifiersComponent } from './verifiers.component';

describe('VerifiersComponent', () => {
  let component: VerifiersComponent;
  let fixture: ComponentFixture<VerifiersComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;
  let authStore: AuthStore;

  class Page extends BasePage<VerifiersComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('form[id="verifiers"] tbody tr');
    }
    get saveBtn() {
      return this.query<HTMLButtonElement>('form[id="verifiers"] button[type="submit"]');
    }
    get discardChangesBtn() {
      return this.queryAll<HTMLButtonElement>('form[id="verifiers"] button[type="button"]')[1];
    }
    get addVerifierSelect() {
      return this.query<HTMLSelectElement>('form[id="add-verifier"] select');
    }
    get addVerifierSelectOptions() {
      return Array.from(this.addVerifierSelect.options).map((option: HTMLOptionElement) => ({
        text: option.textContent.trim(),
        value: option.value,
      }));
    }
    get authorityStatuses() {
      return this.rows.map((row) => row.querySelector<HTMLSelectElement>('select[name$=".authorityStatus"]'));
    }
    get authorityStatusValues() {
      return this.authorityStatuses.map((select) => (select ? this.getInputValue(select) : null));
    }
    set authorityStatusValues(value: string[]) {
      this.authorityStatuses.forEach((select, index) => {
        if (select && value[index] !== undefined) {
          this.setInputValue(`#${select.id}`, value[index]);
        }
      });
    }
    get addVerifierContinue() {
      return this.query<HTMLButtonElement>('form[id="add-verifier"] button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get details() {
      return this.query<HTMLDetailsElement>('details.govuk-details');
    }

    get detailsSummary() {
      return this.query<HTMLSpanElement>('.govuk-details__summary-text');
    }

    get detailsText() {
      return this.queryAll<HTMLParagraphElement>('p');
    }
  }

  const activatedRouteStub = new ActivatedRouteStub(null, null, { ...mockVerifiersRouteData });
  const verifierAuthoritiesService: MockType<VerifierAuthoritiesService> = {
    getVerifierAuthorities: jest.fn().mockReturnValue(asyncData(cloneDeep(mockVerifiersRouteData.verifiers))),
    updateVerifierAuthorities: jest.fn().mockReturnValue(of(null)),
  };
  const vbSiteContactsService: MockType<VBSiteContactsService> = {
    updateVbSiteContacts: jest.fn().mockReturnValue(of(null)),
    getVbSiteContacts: jest.fn().mockReturnValue(of(mockAccountContactVbInfoResponse)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusinessTestingModule, SharedModule, SharedUserModule, RouterTestingModule],
      declarations: [VerifiersComponent, SiteContactsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: VerifierAuthoritiesService, useValue: verifierAuthoritiesService },
        { provide: VBSiteContactsService, useValue: vbSiteContactsService },
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: APP_BASE_HREF, useValue: '/installation-aviation/' },
        DestroySubject,
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
    authStore.setUserState({
      roleType: 'VERIFIER',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
      userId: 'verifierId',
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifiersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render details to reveal more information', () => {
    expect(page.details).toBeTruthy();
    expect(page.detailsSummary.innerHTML.trim()).toEqual('Learn more about user types');
  });

  it('should display more info after expanding the details', () => {
    page.details.click();
    fixture.detectChanges();
    expect(page.detailsText[0].innerHTML.trim()).toEqual(
      'This role can manage verifier users and site contacts and can verify operator AEM reports.',
    );
    expect(page.detailsText[1].innerHTML.trim()).toEqual('This role can verify operator AEM reports only.');
  });

  it('should display the add new user button and select for admin', () => {
    expect(page.addVerifierContinue).toBeTruthy();
    expect(page.addVerifierContinue.innerHTML.trim()).toEqual('Continue');
    expect(page.addVerifierSelect).toBeTruthy();

    expect(page.addVerifierSelectOptions).toEqual([
      { text: 'Verifier admin', value: '0: verifier_admin' },
      { text: 'Verifier', value: '1: verifier' },
    ]);
  });

  it('should navigate to add new user on button click', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.addVerifierContinue.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['add'], { queryParams: { roleCode: 'verifier' }, relativeTo: route });
  });

  it('should display a list of verifiers', () => {
    expect(page.rows).toHaveLength(4);
  });

  it('should render a save and a discard changes button', () => {
    expect(page.saveBtn).toBeTruthy();
    expect(page.saveBtn.innerHTML.trim()).toEqual('Save');

    expect(page.discardChangesBtn).toBeTruthy();
    expect(page.discardChangesBtn.innerHTML.trim()).toEqual('Discard changes');
  });

  it('should not accept submission without at least one active verifier admin', () => {
    page.authorityStatusValues = [undefined, undefined, 'DISABLED'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();

    page.authorityStatusValues = [undefined, undefined, 'ACTIVE'];
    page.saveBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
  });

  it('should update verifier status and refresh lists', async () => {
    changeInputValue(fixture, '#verifiersArray\\.1\\.authorityStatus', 'ACTIVE');
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthorities).not.toHaveBeenCalled();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthorities).toHaveBeenCalledTimes(1);
  });

  it('should post only changed values on save', () => {
    page.authorityStatusValues = [undefined, 'ACTIVE'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthorities).toHaveBeenCalledWith([
      {
        authorityStatus: 'ACTIVE',
        roleCode: 'verifier',
        userId: '1reg',
      },
    ]);
  });

  it('should show an error message when updated user does not exist', async () => {
    verifierAuthoritiesService.updateVerifierAuthorities.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1006' }, status: 400 })),
    );

    changeInputValue(fixture, '#verifiersArray\\.1\\.authorityStatus', 'DISABLED');
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    await expectBusinessErrorToBe(savePartiallyNotFoundVerifierError);
  });

  it('should save site contacts', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];

    component.saveSiteContacts(mockSiteContacts);

    expect(vbSiteContactsService.updateVbSiteContacts).toHaveBeenCalledWith('INSTALLATION', mockSiteContacts);
  });

  it('should display cannot save error page if the body is no longer assigned to the account', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];
    vbSiteContactsService.updateVbSiteContacts.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: { code: ErrorCodes.ACCOUNT1005 },
            status: 400,
          }),
      ),
    );

    component.saveSiteContacts(mockSiteContacts);

    expectBusinessErrorToBe(savePartiallyNotFoundSiteContactsError);
  });

  it('should display cannot save error page if the user is no longer a verifier', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];
    vbSiteContactsService.updateVbSiteContacts.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: { code: ErrorCodes.AUTHORITY1006 },
            status: 400,
          }),
      ),
    );

    component.saveSiteContacts(mockSiteContacts);

    expectBusinessErrorToBe(savePartiallyNotFoundSiteContactsError);
  });
});
