import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { RequestActionStore } from '../store';
import { ThreeYearPeriodOffsettingRequirementsSubmittedComponent } from './3year-period-offsetting-requirements-submitted.component';

describe('AnnualOffsettingRequirementsSummaryActionComponent', () => {
  let component: ThreeYearPeriodOffsettingRequirementsSubmittedComponent;
  let fixture: ComponentFixture<ThreeYearPeriodOffsettingRequirementsSubmittedComponent>;
  let commonStore: CommonActionsStore;
  let store: RequestActionStore;
  let page: Page;
  const route = new ActivatedRouteStub();

  const action = {
    id: 102,
    competentAuthority: 'ENGLAND',
    creationDate: '2026-05-28T11:06:34.545717Z',
    requestAccountId: 6,
    requestId: 'AEM-3YPO-00006-1',
    type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED',
    requestType: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING',
    submitter: 'regulator england',
    payload: {
      aviationAerCorsia3YearPeriodOffsetting: {
        operatorHaveOffsettingRequirements: false,
        periodOffsettingRequirements: 67,
        schemeYears: ['2021', '2022', '2023'],
        yearlyOffsettingData: {
          2021: { calculatedAnnualOffsetting: 111, cefEmissionsReductions: 22 },
          2022: { calculatedAnnualOffsetting: 33, cefEmissionsReductions: 44 },
          2023: { calculatedAnnualOffsetting: 55, cefEmissionsReductions: 66 },
        },
        totalYearlyOffsettingData: { calculatedAnnualOffsetting: 199, cefEmissionsReductions: 132 },
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
  class Page extends BasePage<ThreeYearPeriodOffsettingRequirementsSubmittedComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }
    get summaryValues() {
      return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('tbody th , td , dt , dd')).map(
        (content) => content.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreeYearPeriodOffsettingRequirementsSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      regulatorViewer: false,
      requestActionItem: { ...action },
    });
    commonStore = TestBed.inject(CommonActionsStore);
    commonStore.setState({
      storeInitialized: true,
      action: { ...action },
    });

    fixture = TestBed.createComponent(ThreeYearPeriodOffsettingRequirementsSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('3 year period offsetting requirements submitted');

    expect(page.summaryValues).toEqual([
      ...['2021', '111', '22'],
      ...['2022', '33', '44'],
      ...['2023', '55', '66'],
      ...['Total (tCO2)', '199', '132'],
      ...['Period offsetting requirements (tCO2)', '', '67'],
      ...['Does the operator have any offsetting requirements for this period?', 'No'],
      ...['Users', 'instoper7 aaaaa, Operator admin - Financial contact, Primary contact, Service contact'],
      ...['Name and signature on the official notice', 'Regulator England'],
      ...['Official notice', 'letter-preview.pdf'],
    ]);
  });
});
