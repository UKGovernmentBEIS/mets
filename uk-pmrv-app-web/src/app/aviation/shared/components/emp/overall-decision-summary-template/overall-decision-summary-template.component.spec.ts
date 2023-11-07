import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { EmpReviewDeterminationTypePipe } from '@aviation/shared/pipes/review-determination-type.pipe';
import { BasePage } from '@testing';

import { EmpIssuanceDetermination } from 'pmrv-api';

import { OverallDecisionSummaryTemplateComponent } from './overall-decision-summary-template.component';

class Page extends BasePage<OverallDecisionSummaryTemplateComponent> {
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0], row.querySelectorAll('dd')[1]])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('OverallDecisionSummaryTemplateComponent', () => {
  let component: OverallDecisionSummaryTemplateComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryTemplateComponent>;
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpReviewDeterminationTypePipe, OverallDecisionSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(OverallDecisionSummaryTemplateComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    component.data = { type: 'APPROVED', reason: 'Reason' } as EmpIssuanceDetermination;
    component.isEditable = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(2);
    expect(page.summaryValues).toEqual([
      ['Decision', 'Approve', 'Change'],
      ['Reason for decision', 'Reason', 'Change'],
    ]);
  });
});
