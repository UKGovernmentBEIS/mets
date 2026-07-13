import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { RequestActionStore } from '../store';
import { AnnualOffsettingRequirementsSubmittedComponent } from './annual-offsetting-requirements-submitted.component';

describe('AnnualOffsettingRequirementsSubmittedComponent', () => {
  let component: AnnualOffsettingRequirementsSubmittedComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementsSubmittedComponent>;
  let store: RequestActionStore;
  let commonStore: CommonActionsStore;
  let page: Page;
  const route = new ActivatedRouteStub();

  const action = {
    id: 102,
    requestType: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING',
    type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED',
    payload: {
      aviationAerCorsiaAnnualOffsetting: {
        schemeYear: 2023,
        totalChapter: 12345,
        sectorGrowth: 3.56,
        calculatedAnnualOffsetting: 439,
      },
      decisionNotification: { signatory: 'ce447c34-19a7-4310-84c6-a2931f3ab9fd' },
      officialNotice: {
        name: 'letter-preview.pdf',
        uuid: 'a918c644-eb20-453b-abed-5a555bcfe996',
      },
      usersInfo: {
        'ce447c34-19a7-4310-84c6-a2931f3ab9fd': { name: 'Regulator England' },
        '5c272217-1b33-42ec-9354-c8ea907c7033': {
          name: 'instoper7 aaaaa',
          roleCode: 'operator_admin',
          contactTypes: ['FINANCIAL', 'PRIMARY', 'SERVICE'],
        },
      },
    },
  } as any;
  class Page extends BasePage<AnnualOffsettingRequirementsSubmittedComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnualOffsettingRequirementsSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);

    store.setState({
      requestActionItem: { ...action },
      regulatorViewer: false,
    });
    commonStore = TestBed.inject(CommonActionsStore);
    commonStore.setState({
      storeInitialized: true,
      action: { ...action },
    });

    fixture = TestBed.createComponent(AnnualOffsettingRequirementsSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Annual offsetting requirements submitted');
    expect(page.summaryValues).toEqual([
      ['Scheme year', '2023'],
      ['Total Chapter 3 State Emissions (tCO2)', '12345'],
      ['Sector Growth Value', '3.56%'],
      ['Calculated Annual Offsetting Requirements', '439'],
      ['Users', 'instoper7 aaaaa, Operator admin - Financial contact, Primary contact, Service contact'],
      ['Name and signature on the official notice', 'Regulator England'],
      ['Official notice', 'letter-preview.pdf'],
    ]);
  });
});
