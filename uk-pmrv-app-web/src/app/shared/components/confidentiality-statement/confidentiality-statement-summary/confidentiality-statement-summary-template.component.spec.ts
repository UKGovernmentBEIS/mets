import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ConfidentialityStatementSummaryTemplateComponent } from '@shared/components/confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('ConfidentialityStatementSummaryTemplateComponent', () => {
  let page: Page;
  let component: ConfidentialityStatementSummaryTemplateComponent;
  let fixture: ComponentFixture<ConfidentialityStatementSummaryTemplateComponent>;

  class Page extends BasePage<ConfidentialityStatementSummaryTemplateComponent> {
    get definitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ConfidentialityStatementSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for existing definitions', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        exist: true,
        confidentialSections: [
          {
            explanation: 'explanation 1',
            section: 'section 1',
          },
          {
            explanation: 'explanation 2',
            section: 'section 2',
          },
        ],
      };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.definitions).toHaveLength(4);
      expect(page.definitions).toEqual([
        ['Section', 'section 1'],
        ['Explanation', 'explanation 1'],
        ['Section', 'section 2'],
        ['Explanation', 'explanation 2'],
      ]);
    });
  });

  describe('for no definitions', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = { exist: false };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.definitions).toHaveLength(1);
      expect(page.definitions).toEqual([
        ['Is any of the information in your application commercially confidential?', 'No'],
      ]);
    });
  });
});
