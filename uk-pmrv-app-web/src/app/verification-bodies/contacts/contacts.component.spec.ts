import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of, throwError } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { VerifierAuthoritiesService } from 'pmrv-api';

import { BasePage, mockClass, MockType, RouterStubComponent } from '../../../testing';
import { expectBusinessErrorToBe } from '../../error/testing/business-error';
import { SharedModule } from '../../shared/shared.module';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { savePartiallyNotFoundVerifierError } from '../../verifiers/errors/business-error';
import { verificationBodyContacts } from '../testing/mock-data';
import { ContactsComponent } from './contacts.component';

describe('ContactsComponent', () => {
  let fixture: ComponentFixture<TestComponent>;
  let component: ContactsComponent;
  let testComponent: TestComponent;
  let page: Page;

  const verifierAuthoritiesService: MockType<VerifierAuthoritiesService> = {
    getVerifierAuthoritiesByVerificationBodyId: jest.fn().mockReturnValue(of(verificationBodyContacts)),
    updateVerifierAuthoritiesByVerificationBodyId: jest.fn().mockReturnValue(of(null)),
  };

  @Component({
    template: `
      <app-contacts [verificationBodyId]="verificationBodyId$"></app-contacts>
    `,
  })
  class TestComponent {
    verificationBodyId$: Observable<number>;
  }

  class Page extends BasePage<TestComponent> {
    get verifierAdminButton() {
      return this.query('button');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('form[id="verifiers"] tbody tr');
    }
    get saveBtn() {
      return this.query<HTMLButtonElement>('form[id="verifiers"] button[type="submit"]');
    }
    get discardChangesBtn() {
      return this.queryAll<HTMLButtonElement>('form[id="verifiers"] button[type="button"]')[1];
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
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContactsComponent, TestComponent, RouterStubComponent],
      imports: [SharedModule, RouterTestingModule, SharedUserModule],
      providers: [
        { provide: VerifierAuthoritiesService, useValue: verifierAuthoritiesService },
        { provide: KeycloakService, useValue: mockClass(KeycloakService) },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    testComponent.verificationBodyId$ = of(1);
    component = fixture.debugElement.query(By.directive(ContactsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display button to "Add new verifier admin" and a list of verifiers', () => {
    expect(page.verifierAdminButton).toBeTruthy();
    expect(page.rows).toHaveLength(2);
  });

  it('should render a save and a discard changes button', () => {
    expect(page.saveBtn).toBeTruthy();
    expect(page.saveBtn.innerHTML.trim()).toEqual('Save');

    expect(page.discardChangesBtn).toBeTruthy();
    expect(page.discardChangesBtn.innerHTML.trim()).toEqual('Discard changes');
  });

  it('should not accept submission without at least one active verifier admin', () => {
    page.authorityStatusValues = ['DISABLED', 'ACTIVE'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();

    page.authorityStatusValues = ['ACTIVE', 'DISABLED'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId).toHaveBeenCalledTimes(1);
    expect(verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId).toHaveBeenCalledWith(1, [
      {
        authorityStatus: 'ACTIVE',
        roleCode: 'verifier_admin',
        userId: 'verifier1',
      },
      {
        authorityStatus: 'DISABLED',
        roleCode: 'verifier',
        userId: 'verifier2',
      },
    ]);
    expect(page.errorSummary).toBeFalsy();
  });

  it('should not post save action when pressing save button more than one time', () => {
    page.authorityStatusValues = ['ACTIVE', 'DISABLED'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId).toHaveBeenCalledTimes(1);
    expect(verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId).toHaveBeenCalledWith(1, [
      {
        authorityStatus: 'ACTIVE',
        roleCode: 'verifier_admin',
        userId: 'verifier1',
      },
      {
        authorityStatus: 'DISABLED',
        roleCode: 'verifier',
        userId: 'verifier2',
      },
    ]);
  });

  it('should show an error message when updated user does not exist', async () => {
    verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1006' }, status: 400 })),
    );

    page.authorityStatusValues = ['ACTIVE', 'DISABLED'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    await expectBusinessErrorToBe(savePartiallyNotFoundVerifierError);
  });
});
