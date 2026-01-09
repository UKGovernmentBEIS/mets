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
        providers: [provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonActionsStore);
      store.setState({
        storeInitialized: true,
        action: {
          type: 'EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY',
          requestId: '671',
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
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.heading).toEqual('Information sent to registry by system');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'Environment Agency'],
        ['What is the legal status of your organisation?', 'Limited company'],
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
      expect(page.heading).toEqual('Information sent to registry by system');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'Environment Agency'],
        ['What is the legal status of your organisation?', 'Individual'],
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
      expect(page.heading).toEqual('Information sent to registry by system');
      expect(page.summaryListValues).toHaveLength(8);
      expect(page.summaryListValues).toEqual([
        ['Emitter ID', 'EM00043'],
        ['Emission plan ID', 'UK-E-AV-00043'],
        ['Operator name', 'aviation_ukets20'],
        ['First known aviation activity', '26 Aug 2024'],
        ['Regulator', 'Environment Agency'],
        ['What is the legal status of your organisation?', 'Partnership'],
        ['Name of partnership', 'test fullname'],
        ['Main office address', 'test test'],
      ]);
    });
  });
});
