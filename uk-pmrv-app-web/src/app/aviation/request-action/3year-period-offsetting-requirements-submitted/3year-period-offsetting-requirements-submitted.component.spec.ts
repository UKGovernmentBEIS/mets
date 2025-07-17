import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { RequestActionStore } from '../store';
import { ThreeYearPeriodOffsettingRequirementsSubmittedComponent } from './3year-period-offsetting-requirements-submitted.component';

describe('AnnualOffsettingRequirementsSummaryActionComponent', () => {
  let component: ThreeYearPeriodOffsettingRequirementsSubmittedComponent;
  let fixture: ComponentFixture<ThreeYearPeriodOffsettingRequirementsSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

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
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED',
        creationDate: '2024-10-15T12:12:48.469862Z',
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
        } as unknown as AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload,
      },
      regulatorViewer: false,
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
    ]);
  });
});
