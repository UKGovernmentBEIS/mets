import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { OperatorDetailsComponent } from '@aviation/request-action/emp/ukets/tasks/operator-details/operator-details.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { OrganisationStructure } from 'pmrv-api';

class Page extends BasePage<OperatorDetailsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('OperatorDetailsComponent', () => {
  let component: OperatorDetailsComponent;
  let fixture: ComponentFixture<OperatorDetailsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, OperatorDetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          emissionsMonitoringPlan: {
            operatorDetails: {
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
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(OperatorDetailsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Operator details');
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
