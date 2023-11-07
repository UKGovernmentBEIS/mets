import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { BasePage } from '@testing';

describe('AerMonitoringPlanChangesSummaryTemplateComponent', () => {
  let page: Page;
  let component: AerMonitoringPlanChangesSummaryTemplateComponent;
  let fixture: ComponentFixture<AerMonitoringPlanChangesSummaryTemplateComponent>;

  class Page extends BasePage<AerMonitoringPlanChangesSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerMonitoringPlanChangesSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AerMonitoringPlanChangesSummaryTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      notCoveredChangesExist: true,
      details: 'My details',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(2);
    expect(page.summaryValues).toEqual([
      ['Were there any changes not covered by the emissions monitoring plans in the reporting year?', 'Yes'],
      ['Changes reported by the operator', 'My details'],
    ]);
  });
});
