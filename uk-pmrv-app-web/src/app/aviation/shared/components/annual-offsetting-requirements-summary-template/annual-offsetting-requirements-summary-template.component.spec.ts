import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { AnnualOffsettingRequirementsSummaryTemplateComponent } from './annual-offsetting-requirements-summary-template.component';

describe('AnnualOffsettingRequirementsSummaryTemplateComponent', () => {
  let component: AnnualOffsettingRequirementsSummaryTemplateComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementsSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<AnnualOffsettingRequirementsSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnualOffsettingRequirementsSummaryTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AnnualOffsettingRequirementsSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.annualOffsetting = {
      schemeYear: 2023,
      totalChapter: 12345,
      sectorGrowth: 3.56,
      calculatedAnnualOffsetting: 439,
    };
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(4);
    expect(page.summaryValues).toEqual([
      ['Scheme year', '2023'],
      ['Total Chapter 3 State Emissions (tCO2)', '12345'],
      ['Sector Growth Value', '3.56%'],
      ['Calculated Annual Offsetting Requirements', '439'],
    ]);
  });
});
