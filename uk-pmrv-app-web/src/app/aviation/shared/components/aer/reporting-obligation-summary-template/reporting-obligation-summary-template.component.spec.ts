import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { BasePage } from '@testing';

describe('ReportingObligationSummaryTemplateComponent', () => {
  let page: Page;
  let component: ReportingObligationSummaryTemplateComponent;
  let fixture: ComponentFixture<ReportingObligationSummaryTemplateComponent>;

  class Page extends BasePage<ReportingObligationSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportingObligationSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReportingObligationSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for reporting obligation', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.reportingData = {
        reportingRequired: true,
        reportingObligationDetails: null,
      };
      component.year = 2023;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(1);
      expect(page.summaryValues).toEqual([['Are you required to complete a 2023 emissions report?', 'Yes']]);
    });
  });

  describe('for no reporting obligation', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.reportingData = {
        reportingRequired: false,
        reportingObligationDetails: {
          noReportingReason: 'No reason',
          supportingDocuments: [],
        },
      };
      component.year = 2023;
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
        ['Are you required to complete a 2023 emissions report?', 'No'],
        ['Reasons', 'No reason'],
      ]);
    });
  });
});
