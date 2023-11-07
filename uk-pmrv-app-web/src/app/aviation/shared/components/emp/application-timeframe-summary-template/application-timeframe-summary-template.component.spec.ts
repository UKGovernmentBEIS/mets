import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ApplicationTimeframeSummaryTemplateComponent } from '@aviation/shared/components/emp/application-timeframe-summary-template/application-timeframe-summary-template.component';
import { BasePage } from '@testing';

describe('ApplicationTimeframeSummaryTemplateComponent', () => {
  let page: Page;
  let component: ApplicationTimeframeSummaryTemplateComponent;
  let fixture: ComponentFixture<ApplicationTimeframeSummaryTemplateComponent>;

  class Page extends BasePage<ApplicationTimeframeSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicationTimeframeSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ApplicationTimeframeSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for late submission', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        dateOfStart: '2023-01-01',
        submittedOnTime: false,
        reasonForLateSubmission: 'My reason',
      };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(3);
      expect(page.summaryValues).toEqual([
        ['When did you become a UK ETS aircraft operator?', '1 January 2023'],
        ['Are you submitting your application within 42 days of this date?', 'No'],
        ['Reason for late submission', 'My reason'],
      ]);
    });
  });

  describe('for no late submission', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        dateOfStart: '2023-01-01',
        submittedOnTime: true,
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
        ['When did you become a UK ETS aircraft operator?', '1 January 2023'],
        ['Are you submitting your application within 42 days of this date?', 'Yes'],
      ]);
    });
  });
});
