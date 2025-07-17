import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { ErrorCodes } from '@error/business-errors';
import { SharedModule } from '@shared/shared.module';
import { BasePage, MockType } from '@testing';

import { CompanyInformationService } from 'pmrv-api';

import { legalEntityFormOpFactory } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';
import { LegalEntityRegnoOpComponent } from './legal-entity-regno-op.component';

describe('CompetentAuthorityGuidanceComponent', () => {
  let component: LegalEntityRegnoOpComponent;
  let fixture: ComponentFixture<LegalEntityRegnoOpComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  let companyInformationService: MockType<CompanyInformationService>;
  let countryService: MockType<CountryService>;

  class Page extends BasePage<LegalEntityRegnoOpComponent> {
    get isEntityRegisteredRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="isEntityRegistered"]');
    }

    get referenceNumber() {
      return this.getInputValue('#referenceNumber');
    }

    set referenceNumber(value: string) {
      this.setInputValue('#referenceNumber', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }
  beforeEach(async () => {
    companyInformationService = { getCompanyProfileByRegistrationNumber: jest.fn().mockReturnValue(of([])) };

    countryService = { getCountryCode: jest.fn().mockReturnValue(of(null)) };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [LegalEntityRegnoOpComponent],
      providers: [
        { provide: CompanyInformationService, useValue: companyInformationService },
        { provide: CountryService, useValue: countryService },
        legalEntityFormOpFactory,
        InstallationAccountApplicationStore,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);

    TestBed.inject(ActivatedRoute).data = of({ legalEntities: [{ id: 1, name: 'Legal Entity' }] });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalEntityRegnoOpComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`when entering company number that exists, should submit a valid form and navigate to next route`, () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    companyInformationService.getCompanyProfileByRegistrationNumber.mockReturnValueOnce(
      of({
        name: 'Company Name',
        registrationNumber: '12345678',
        address: {
          line1: '1 Street',
          city: 'City',
          country: 'United Kingdom',
          postcode: 'AB1 2CD',
        },
      }),
    );

    countryService.getCountryCode.mockReturnValueOnce(of('GB'));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList.length).toEqual(1);

    page.isEntityRegisteredRadios[0].click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Enter your company registration number']);

    page.referenceNumber = '123';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(companyInformationService.getCompanyProfileByRegistrationNumber).toHaveBeenCalledTimes(1);
    expect(companyInformationService.getCompanyProfileByRegistrationNumber).toHaveBeenCalledWith('123');

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../details'], { relativeTo: route });
  });

  it(`when entering company number that doesnt exist, should handle 404 error and display error message`, () => {
    const errorResponse = { error: { code: ErrorCodes.NOTFOUND1001 } };
    companyInformationService.getCompanyProfileByRegistrationNumber.mockReturnValue(throwError(() => errorResponse));

    page.isEntityRegisteredRadios[0].click();
    fixture.detectChanges();

    page.referenceNumber = '12345678';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toContain('The registration number was not found at the Companies House');
    expect(component.form.get('referenceNumber').hasError('referenceNumberNotExists')).toBeTruthy();
  });
});
