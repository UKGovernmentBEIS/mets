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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActionSharedModule, PipesModule, SharedModule],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        requestId: 'HSETI00164-2021_2025',
        payload: {
          payloadType: 'PERMIT_ISSUANCE_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD',
          activePermit: {
            emitterId: 'EM00206',
            permitId: 'UK-E-IN-00206',
            installationName: 'operator 36 onshore',
            operatorName: 'operator36',
            regulator: 'EA',
            firstYearOfReportingObligation: 2022,
            regulatedActivity: ['AMMONIA_PRODUCTION'],
          },
          organizationDetails: {
            organisationLegalStatus: 'LIMITED_COMPANY',
            registeredAddress: {
              line1: '108 Navigation Walk',
              line2: '2323',
              city: '2442',
              country: 'BY',
              postcode: '124',
            },
            companyRegistrationNumber: '11112233',
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
    expect(page.summaryListValues).toHaveLength(10);
    expect(page.summaryListValues).toEqual([
      ['Emitter ID', 'EM00206'],
      ['Permit ID', 'UK-E-IN-00206'],
      ['Installation name', 'operator 36 onshore'],
      ['Operator name', 'operator36'],
      ['Regulator', 'EA'],
      ['First year of Registry reporting obligation', '2022'],
      ['Regulated activity', 'Ammonia production'],
      ['Organisation legal status', 'Limited Company'],
      ['Company registration number', '11112233'],
      ['Registered address', '108 Navigation Walk , 23232442124'],
    ]);
  });
});
