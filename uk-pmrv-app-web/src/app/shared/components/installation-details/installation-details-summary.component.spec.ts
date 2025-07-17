import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CountryService } from '@core/services/country.service';
import { UserState } from '@core/store';
import { CoordinatePipe } from '@shared/pipes/coordinate.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage, CountryServiceStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CompanyProfileDTO, InstallationOperatorDetails, LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import {
  mockOffshore,
  mockOnshore,
  mockPermitApplyPayload,
} from '../../../permit-application/testing/mock-permit-apply-action';
import { InstallationDetailsSummaryComponent } from './installation-details-summary.component';

describe('InstallationDetailsSummaryComponent', () => {
  let component: InstallationDetailsSummaryComponent;
  let fixture: ComponentFixture<InstallationDetailsSummaryComponent>;
  let page: Page;
  let coordinatePipe: CoordinatePipe;

  class Page extends BasePage<InstallationDetailsSummaryComponent> {
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get summaryPairTextCompaniesHouse() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), ...row.querySelectorAll('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const mock = mockPermitApplyPayload.installationOperatorDetails;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        CoordinatePipe,
        GovukDatePipe,
        KeycloakService,
        { provide: CountryService, useClass: CountryServiceStub },
      ],
    }).compileComponents();
  });

  const createComponent = (
    installationOperatorDetails?: InstallationOperatorDetails,
    companiesHouse?: CompanyProfileDTO,
    roleType?: UserState['roleType'],
  ) => {
    fixture = TestBed.createComponent(InstallationDetailsSummaryComponent);
    component = fixture.componentInstance;
    component.installationOperatorDetails = installationOperatorDetails ? installationOperatorDetails : mock;
    component.companiesHouse = companiesHouse;
    component.roleType = roleType;
    page = new Page(fixture);
    coordinatePipe = TestBed.inject(CoordinatePipe);
    fixture.detectChanges();
  };

  beforeEach(() => createComponent());

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details for onshore', () => {
    const location = mockOnshore.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mock.installationName],
      ['Site name', mock.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],
      ['Company registration number', mock.companyReferenceNumber],
      ['Operator name', mock.operator],
      ['Legal status', 'Limited Company'],
      [
        'Operator address',
        `${mock.operatorDetailsAddress.line1}, ${mock.operatorDetailsAddress.line2} ${mock.operatorDetailsAddress.city}${mock.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });

  it('should display the details for companiesHouse', async () => {
    createComponent(
      mockOnshore,
      {
        name: 'COMPANY 91634248 LIMITED',
        registrationNumber: '91634248',
        address: {
          line1: 'Companies House',
          line2: 'Crownway',
          city: 'Cardiff',
          country: 'United Kingdom',
          postcode: 'CF14 3UZ',
        },
      },
      'REGULATOR',
    );
    const location = mockOnshore.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairTextCompaniesHouse).toEqual([
      ['Installation name', mock.installationName],
      ['Site name', mock.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],
      ['Details', 'Current input', 'Companies house input'],
      ['Company registration number', mock.companyReferenceNumber, '91634248'],
      ['Operator name', mock.operator, 'COMPANY 91634248 LIMITED'],
      ['Legal status', 'Limited Company', ''],
      [
        'Operator address',
        `${mock.operatorDetailsAddress.line1}, ${mock.operatorDetailsAddress.line2} ${mock.operatorDetailsAddress.city}${mock.operatorDetailsAddress.postcode}Greece`,
        'Companies House , Crownway CardiffCF14 3UZUnited Kingdom',
      ],
    ]);
  });

  it('should display the details for offshore', () => {
    createComponent(mockOffshore);
    const location = mockOffshore.installationLocation as LocationOffShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mock.installationName],
      ['Site name', mock.siteName],
      [
        'Coordinates',
        `Latitude${coordinatePipe.transform(location.latitude)}Longitude${coordinatePipe.transform(
          location.longitude,
        )}`,
      ],

      ['Company registration number', mock.companyReferenceNumber],
      ['Operator name', mock.operator],
      ['Legal status', 'Limited Company'],
      [
        'Operator address',
        `${mock.operatorDetailsAddress.line1}, ${mock.operatorDetailsAddress.line2} ${mock.operatorDetailsAddress.city}${mock.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });
});
