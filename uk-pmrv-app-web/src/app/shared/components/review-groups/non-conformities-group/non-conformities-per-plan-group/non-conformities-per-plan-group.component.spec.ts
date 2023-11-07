import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NonConformitiesPerPlanGroupComponent } from '@shared/components/review-groups/non-conformities-group/non-conformities-per-plan-group/non-conformities-per-plan-group.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('NonConformitiesPerPlanGroupComponent', () => {
  let page: Page;
  let component: NonConformitiesPerPlanGroupComponent;
  let fixture: ComponentFixture<NonConformitiesPerPlanGroupComponent>;

  class Page extends BasePage<NonConformitiesPerPlanGroupComponent> {
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
      areTherePriorYearIssues: false,
      areThereUncorrectedNonConformities: true,
      uncorrectedNonConformities: [
        {
          reference: 'B1',
          explanation: 'Explanation 1',
          materialEffect: true,
        },
        {
          reference: 'B2',
          explanation: 'Explanation 2',
          materialEffect: false,
        },
      ],
    };
    component.isEditable = true;
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonConformitiesPerPlanGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true, with data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonConformitiesPerPlanGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-conformities-per-plan-group`, `change`, `delete` and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Non-conformities with the approved monitoring plan');

      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-conformities with the approved monitoring plan?',
        'Yes',
        'Change',
      ]);
      expect(page.summaryTable).toEqual([
        'B1',
        'Explanation 1',
        'Material',
        'Change',
        'Remove',
        'B2',
        'Explanation 2',
        'Immaterial',
        'Change',
        'Remove',
      ]);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is true, without data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonConformitiesPerPlanGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.uncorrectedNonConformities.areThereUncorrectedNonConformities = false;
      component.uncorrectedNonConformities.uncorrectedNonConformities = null;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-conformities-per-plan-group`, `change`, `delete` and add buttons', () => {
      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-conformities with the approved monitoring plan?',
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
      fixture = TestBed.createComponent(NonConformitiesPerPlanGroupComponent);
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
      expect(page.heading2.textContent.trim()).toEqual('Non-conformities with the approved monitoring plan');
      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-conformities with the approved monitoring plan?',
        'Yes',
      ]);
      expect(page.summaryTable).toEqual([
        'B1',
        'Explanation 1',
        'Material',
        '',
        '',
        'B2',
        'Explanation 2',
        'Immaterial',
        '',
        '',
      ]);
      expect(page.addButton).toBeFalsy();
    });
  });
});
