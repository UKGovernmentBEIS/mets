import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { ReviewGroupDecisionSharedComponent } from './review-group-decision.component';

describe('ReviewGroupDecisionComponent', () => {
  let component: ReviewGroupDecisionSharedComponent;
  let fixture: ComponentFixture<ReviewGroupDecisionSharedComponent>;
  let page: Page;

  class Page extends BasePage<ReviewGroupDecisionSharedComponent> {
    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="decision"]');
    }

    get notes() {
      return this.getInputValue('#notes');
    }

    set notes(value: string) {
      this.setInputValue('#notes', value);
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get header() {
      return this.query<HTMLHeadingElement>('fieldset legend').textContent.trim();
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewGroupDecisionSharedComponent);
    component = fixture.componentInstance;
    component.requestTaskId = 1;
    component.isEditable = true;
    component.payload = {
      regulatorReviewAttachments: {},
      regulatorReviewSectionsCompleted: { ALC: false },
      regulatorReviewGroupDecisions: {},
      regulatorReviewOutcome: {},
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewGroupDecisionSharedComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  describe('for new decision', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.header).toEqual('What is your decision on the information submitted?');
      expect(page.summaryListValues).toEqual([]);
      expect(page.decisionRadioButtons.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });
  });
});
