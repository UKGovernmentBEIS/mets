import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { RequestActionStore } from '../store';
import { AnnualOffsettingRequirementsSubmittedComponent } from './annual-offsetting-requirements-submitted.component';

describe('AnnualOffsettingRequirementsSubmittedComponent', () => {
  let component: AnnualOffsettingRequirementsSubmittedComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementsSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

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
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED',
        creationDate: '2024-10-15T12:12:48.469862Z',
        payload: {
          aviationAerCorsiaAnnualOffsetting: {
            schemeYear: 2023,
            totalChapter: 12345,
            sectorGrowth: 3.56,
            calculatedAnnualOffsetting: 439,
          },
        } as AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload,
      },
      regulatorViewer: false,
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
    ]);
  });
});
