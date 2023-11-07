import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { EmissionFactorsSummaryTemplateComponent } from './emission-factors-summary-template.component';

describe('EmissionFactorsSummaryTemplateComponent', () => {
  let page: Page;
  let component: EmissionFactorsSummaryTemplateComponent;
  let fixture: ComponentFixture<EmissionFactorsSummaryTemplateComponent>;

  class Page extends BasePage<EmissionFactorsSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionFactorsSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionFactorsSummaryTemplateComponent);
    component = fixture.componentInstance;

    component.fuelTypes = [
      {
        id: 'AVIATION_GASOLINE',
        key: 'Aviation gasoline (AV gas)',
        value: '3.10 tCO2 per tonne of fuel',
      },
    ];
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(1);
    expect(page.summaryValues).toEqual([['Aviation gasoline (AV gas)', '3.10 tCO2 per tonne of fuel']]);
  });
});
