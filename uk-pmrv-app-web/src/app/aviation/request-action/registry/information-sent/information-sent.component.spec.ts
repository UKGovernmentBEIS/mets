import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsState } from '@actions/store/common-actions.state';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { InformationSentToRegistryComponent } from './information-sent.component';

describe('InformationSentToRegistryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: InformationSentToRegistryComponent;
  let fixture: ComponentFixture<InformationSentToRegistryComponent>;

  class Page extends BasePage<InformationSentToRegistryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  describe('LIMITED_COMPANY', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ActionSharedModule, PipesModule, SharedModule],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY',
          requestId: '671',
          submitter: 'system',
          payload: {
            payloadType: 'EMP_ISSUANCE_UKETS_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD',
            operatorDetails: {
              emitterId: 'EM00043',
              regulator: 'ENGLAND',
              operatorName: 'aviation_ukets20',
              emissionsPlanId: 'UK-E-AV-00043',
              firstKnownAviationActivity: '2024-08-26',
            },
            organisationDetails: {
              registeredAddress: {
                city: 'test',
                type: 'ONSHORE_STATE',
                line1: 'test',
                country: 'GH',
              },
              organisationLegalStatus: 'LIMITED_COMPANY',
              companyRegistrationNumber: 'test',
            },
          },
        },
      } as CommonActionsState);

      fixture = TestBed.createComponent(InformationSentToRegistryComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      page = new Page(fixture);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to Registry by system');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'ENGLAND'],
        ['Organisation legal status', 'Limited company'],
        ['Company registration number', 'test'],
        ['Registered address', 'test test'],
      ]);
    });
  });
  describe('INDIVIDUAL', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ActionSharedModule, PipesModule, SharedModule],
        providers: [provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY',
          requestId: '671',
          submitter: 'system',
          payload: {
            payloadType: 'EMP_ISSUANCE_UKETS_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD',
            operatorDetails: {
              emitterId: 'EM00043',
              regulator: 'ENGLAND',
              operatorName: 'aviation_ukets20',
              emissionsPlanId: 'UK-E-AV-00043',
              firstKnownAviationActivity: '2024-08-26',
            },
            organisationDetails: {
              address: {
                city: 'test',
                type: 'ONSHORE_STATE',
                line1: 'test',
                country: 'GH',
              },
              organisationLegalStatus: 'INDIVIDUAL',
              fullName: 'test fullname',
            },
          },
        },
      } as CommonActionsState);

      fixture = TestBed.createComponent(InformationSentToRegistryComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to Registry by system');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'ENGLAND'],
        ['Organisation legal status', 'Individual'],
        ['Full name', 'test fullname'],
        ['Address', 'test test'],
      ]);
    });
  });

  describe('PARTNERSHIP', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ActionSharedModule, PipesModule, SharedModule],
        providers: [provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY',
          requestId: '671',
          submitter: 'Regulator England',
          payload: {
            payloadType: 'EMP_ISSUANCE_UKETS_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD',
            operatorDetails: {
              emitterId: 'EM00043',
              regulator: 'ENGLAND',
              operatorName: 'aviation_ukets20',
              emissionsPlanId: 'UK-E-AV-00043',
              firstKnownAviationActivity: '2024-08-26',
            },
            organisationDetails: {
              mainOfficeAddress: {
                city: 'test',
                type: 'ONSHORE_STATE',
                line1: 'test',
                country: 'GH',
              },
              organisationLegalStatus: 'PARTNERSHIP',
              partnershipName: 'test fullname',
            },
          },
        },
      } as CommonActionsState);

      fixture = TestBed.createComponent(InformationSentToRegistryComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to Registry by Regulator England');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'ENGLAND'],
        ['Organisation legal status', 'Partnership'],
        ['Name of partnership', 'test fullname'],
        ['Main office address', 'test test'],
      ]);
    });
  });

  describe('account update LIMITED_COMPANY', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ActionSharedModule, PipesModule, SharedModule],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY',
          requestId: '671',
          submitter: 'system',
          payload: {
            payloadType: 'EMP_VARIATION_UKETS_REGISTRY_INTEGRATION_ACCOUNT_UPDATED_PAYLOAD',
            operatorDetails: {
              registryId: '1234567',
              operatorName: 'aviation_ukets20',
              emissionsPlanId: 'UK-E-AV-00043',
              firstYearOfReportingObligation: '2024',
            },
            organisationDetails: {
              registeredAddress: {
                city: 'test',
                type: 'ONSHORE_STATE',
                line1: 'test',
                country: 'GH',
              },
              organisationLegalStatus: 'LIMITED_COMPANY',
              companyRegistrationNumber: 'test',
            },
          },
        },
      } as CommonActionsState);

      fixture = TestBed.createComponent(InformationSentToRegistryComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      page = new Page(fixture);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to Registry by system');
      expect(page.summaryListValues).toHaveLength(7);
      expect(page.summaryListValues).toEqual([
        ['UK ETS Registry ID', '1234567'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First year of reporting obligation', '2024'],
        ['Organisation legal status', 'Limited company'],
        ['Company registration number', 'test'],
        ['Registered address', 'test test'],
      ]);
    });
  });

  describe('account update LIMITED_COMPANY for Aviation only', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ActionSharedModule, PipesModule, SharedModule],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY',
          requestId: '671',
          submitter: 'system',
          payload: {
            payloadType: 'AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY_PAYLOAD',
            registryId: 1234567,
            reportableEmissions: '1000',
            reportingYear: 2024,
            organisationDetails: {
              registeredAddress: {
                city: 'test',
                type: 'ONSHORE_STATE',
                line1: 'test',
                country: 'GH',
              },
              organisationLegalStatus: 'LIMITED_COMPANY',
              companyRegistrationNumber: 'test',
            },
          },
        },
      } as CommonActionsState);

      fixture = TestBed.createComponent(InformationSentToRegistryComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      page = new Page(fixture);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to Registry by system');
      expect(page.summaryListValues).toHaveLength(3);
      expect(page.summaryListValues).toEqual([
        ['UK ETS Registry ID', '1234567'],
        ['Reporting year', '2024'],
        ['Emissions value', '1000 tCO2'],
      ]);
    });
  });
});
