import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { BasePage } from '@testing';

import { OrganisationStructure } from 'pmrv-api';

describe('OperatorDetailsSummaryTemplateComponent', () => {
  let page: Page;
  let component: OperatorDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<OperatorDetailsSummaryTemplateComponent>;

  class Page extends BasePage<OperatorDetailsSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(OperatorDetailsSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for limited company', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        crcoCode: 'CRCO001',
        operatorName: 'AviationName001',
        operatingLicense: {
          licenseExist: false,
        },
        flightIdentification: {
          icaoDesignators: 'D-CAPB\nD-CAWR',
          flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
        },
        organisationStructure: {
          evidenceFiles: ['b686172e-aa8b-4e27-a2a7-c9fd381b2f88'],
          legalStatusType: 'LIMITED_COMPANY',
          registrationNumber: 'My number',
          organisationLocation: {
            city: 'town',
            type: 'ONSHORE_STATE',
            line1: 'My address 1',
            line2: 'My address 2',
            state: 'My state',
            country: 'GR',
            postcode: '11344',
          },
          differentContactLocationExist: false,
        } as OrganisationStructure,
        activitiesDescription: {
          flightTypes: ['SCHEDULED'],
          operatorType: 'COMMERCIAL',
          operationScopes: ['UK_DOMESTIC'],
          activityDescription: 'My activities',
        },
        airOperatingCertificate: {
          certificateExist: false,
        },
      };
      component.evidenceFiles = [{ fileName: 'test.png', downloadUrl: 'link' }];

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(15);
      expect(page.summaryValues).toEqual([
        ['Aircraft operator name', 'AviationName001'],
        ['CRCO code', 'CRCO001'],
        [
          'What call sign identification do you use for Air Traffic Control purposes?',
          'International Civil Aviation Organisation (ICAO) designators',
        ],
        ['ICAO designators used', 'D-CAPB\nD-CAWR'],
        ['Do you have an Air Operator Certificate (AOC) or equivalent?', 'No'],
        ['Do you have an operating licence?', 'No'],
        ['What is the legal status of your organisation?', 'Limited company'],
        ['Company registration number', 'My number'],
        ['Registered address', 'My address 1  , My address 2 townMy state11344'],
        ['Proof of registered address', 'test.png'],
        ['Do you want to provide a different contact address?', 'No'],
        ['Type of operator', 'Commercial'],
        ['Type of flights', 'Scheduled flights'],
        ['Scope of operation', 'UK domestic'],
        ['Describe your activities', 'My activities'],
      ]);
    });
  });

  describe('for individual', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        crcoCode: 'CRCO001',
        operatorName: 'AviationName001',
        operatingLicense: {
          licenseExist: false,
        },
        flightIdentification: {
          icaoDesignators: 'D-CAPB\nD-CAWR',
          flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
        },
        organisationStructure: {
          fullName: 'My name',
          legalStatusType: 'INDIVIDUAL',
          organisationLocation: {
            city: 'town',
            type: 'ONSHORE_STATE',
            line1: 'My address 1',
            line2: 'My address 2',
            state: 'My state',
            country: 'GR',
            postcode: '11344',
          },
        } as OrganisationStructure,
        activitiesDescription: {
          flightTypes: ['SCHEDULED'],
          operatorType: 'COMMERCIAL',
          operationScopes: ['UK_DOMESTIC'],
          activityDescription: 'My activities',
        },
        airOperatingCertificate: {
          certificateExist: false,
        },
      };

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(13);
      expect(page.summaryValues).toEqual([
        ['Aircraft operator name', 'AviationName001'],
        ['CRCO code', 'CRCO001'],
        [
          'What call sign identification do you use for Air Traffic Control purposes?',
          'International Civil Aviation Organisation (ICAO) designators',
        ],
        ['ICAO designators used', 'D-CAPB\nD-CAWR'],
        ['Do you have an Air Operator Certificate (AOC) or equivalent?', 'No'],
        ['Do you have an operating licence?', 'No'],
        ['What is the legal status of your organisation?', 'Individual'],
        ['Full name', 'My name'],
        ['Contact address', 'My address 1  , My address 2 townMy state11344'],
        ['Type of operator', 'Commercial'],
        ['Type of flights', 'Scheduled flights'],
        ['Scope of operation', 'UK domestic'],
        ['Describe your activities', 'My activities'],
      ]);
    });
  });

  describe('for partnership', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        crcoCode: 'CRCO001',
        operatorName: 'AviationName001',
        operatingLicense: {
          licenseExist: false,
        },
        flightIdentification: {
          icaoDesignators: 'D-CAPB\nD-CAWR',
          flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
        },
        organisationStructure: {
          partners: ['partner 1', 'partner 2'],
          partnershipName: 'My partnership',
          legalStatusType: 'PARTNERSHIP',
          organisationLocation: {
            city: 'town',
            type: 'ONSHORE_STATE',
            line1: 'My address 1',
            line2: 'My address 2',
            state: 'My state',
            country: 'GR',
            postcode: '11344',
          },
        } as OrganisationStructure,
        activitiesDescription: {
          flightTypes: ['SCHEDULED'],
          operatorType: 'COMMERCIAL',
          operationScopes: ['UK_DOMESTIC'],
          activityDescription: 'My activities',
        },
        airOperatingCertificate: {
          certificateExist: false,
        },
      };

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(14);
      expect(page.summaryValues).toEqual([
        ['Aircraft operator name', 'AviationName001'],
        ['CRCO code', 'CRCO001'],
        [
          'What call sign identification do you use for Air Traffic Control purposes?',
          'International Civil Aviation Organisation (ICAO) designators',
        ],
        ['ICAO designators used', 'D-CAPB\nD-CAWR'],
        ['Do you have an Air Operator Certificate (AOC) or equivalent?', 'No'],
        ['Do you have an operating licence?', 'No'],
        ['What is the legal status of your organisation?', 'Partnership'],
        ['Name of partnership', 'My partnership'],
        ['Main office address', 'My address 1  , My address 2 townMy state11344'],
        ['List the partners', 'partner 1partner 2'],
        ['Type of operator', 'Commercial'],
        ['Type of flights', 'Scheduled flights'],
        ['Scope of operation', 'UK domestic'],
        ['Describe your activities', 'My activities'],
      ]);
    });
  });
});
