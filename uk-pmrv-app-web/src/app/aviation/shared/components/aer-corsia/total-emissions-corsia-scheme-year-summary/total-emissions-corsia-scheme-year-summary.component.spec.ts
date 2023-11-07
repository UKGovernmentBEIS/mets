import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from './total-emissions-corsia-scheme-year-summary.component';

describe('TotalEmissionsCorsiaSchemeYearSummaryComponent', () => {
  let page: Page;
  let component: TotalEmissionsCorsiaSchemeYearSummaryComponent;
  let fixture: ComponentFixture<TotalEmissionsCorsiaSchemeYearSummaryComponent>;

  class Page extends BasePage<TotalEmissionsCorsiaSchemeYearSummaryComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsCorsiaSchemeYearSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TotalEmissionsCorsiaSchemeYearSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct values when totalEmissions$ is set', () => {
    component.totalEmissions = {
      allFlightsEmissions: '35650',
      allFlightsNumber: 2987,
      offsetFlightsEmissions: '1133',
      offsetFlightsNumber: 1240,
      nonOffsetFlightsEmissions: '19602',
      nonOffsetFlightsNumber: 1747,
      emissionsReductionClaim: '0',
    };
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Emissions from all international flights',
      '35650 tCO2 from 2987 flights',
      'Emissions from flights with offsetting requirements',
      '1133 tCO2 from 1240 flights',
      'Emissions from flights with no offsetting requirements',
      '19602 tCO2 from 1747 flights',
      'Emissions reduction claim from CORSIA eligible fuels',
      '0 tCO2',
    ]);
  });
});
