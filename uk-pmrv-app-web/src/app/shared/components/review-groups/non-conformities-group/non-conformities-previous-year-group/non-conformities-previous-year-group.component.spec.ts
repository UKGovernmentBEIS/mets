import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NonConformitiesPreviousYearGroupComponent } from '@shared/components/review-groups/non-conformities-group/non-conformities-previous-year-group/non-conformities-previous-year-group.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('NonConformitiesPreviousYearGroupComponent', () => {
  let page: Page;
  let component: NonConformitiesPreviousYearGroupComponent;
  let fixture: ComponentFixture<NonConformitiesPreviousYearGroupComponent>;

  class Page extends BasePage<NonConformitiesPreviousYearGroupComponent> {
    get heading2(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h2');
    }

    get summaryListGuardQuestion(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((dt) => dt.textContent.trim());
    }

    get summaryTable(): string[] {
      return this.queryAll<HTMLTableCellElement>('tr td').map((td) => td.textContent.trim());
    }

    get addButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  const initComponentState = () => {
    component.uncorrectedNonConformities = {
      areThereUncorrectedNonConformities: false,
      areTherePriorYearIssues: true,
      priorYearIssues: [
        {
          reference: 'E1',
          explanation: 'Explanation 1',
        },
        {
          reference: 'E2',
          explanation: 'Explanation 2',
        },
      ],
    };
    component.isEditable = true;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonConformitiesPreviousYearGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true, with data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonConformitiesPreviousYearGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-conformities-previous-year-group`, `change`, `delete` and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual(
        'Non-conformities from the previous year that have not been resolved',
      );
      expect(page.summaryListGuardQuestion).toEqual([
        'Are there any unresolved non-conformities from a previous year?',
        'Yes',
        'Change',
      ]);
      expect(page.summaryTable).toEqual([
        'E1',
        'Explanation 1',
        'Change',
        'Remove',
        'E2',
        'Explanation 2',
        'Change',
        'Remove',
      ]);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is true, without data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonConformitiesPreviousYearGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.uncorrectedNonConformities.areTherePriorYearIssues = false;
      component.uncorrectedNonConformities.priorYearIssues = null;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-conformities-previous-year-group`, `change`, `delete` and add buttons', () => {
      expect(page.summaryListGuardQuestion).toEqual([
        'Are there any unresolved non-conformities from a previous year?',
        'No',
        'Change',
      ]);
      expect(page.summaryTable).toEqual([]);
      expect(page.heading2).toBeTruthy();
      expect(page.addButton).toBeFalsy();
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonConformitiesPreviousYearGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the regulated activities and not render delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual(
        'Non-conformities from the previous year that have not been resolved',
      );
      expect(page.summaryListGuardQuestion).toEqual([
        'Are there any unresolved non-conformities from a previous year?',
        'Yes',
      ]);
      expect(page.summaryTable).toEqual(['E1', 'Explanation 1', '', '', 'E2', 'Explanation 2', '', '']);
      expect(page.addButton).toBeFalsy();
    });
  });
});
