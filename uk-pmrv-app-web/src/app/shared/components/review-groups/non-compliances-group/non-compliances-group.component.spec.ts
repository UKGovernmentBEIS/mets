import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NonCompliancesGroupComponent } from '@shared/components/review-groups/non-compliances-group/non-compliances-group.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('NonCompliancesGroupComponent', () => {
  let page: Page;
  let component: NonCompliancesGroupComponent;
  let fixture: ComponentFixture<NonCompliancesGroupComponent>;

  class Page extends BasePage<NonCompliancesGroupComponent> {
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
    component.uncorrectedNonCompliances = {
      areThereUncorrectedNonCompliances: true,
      uncorrectedNonCompliances: [
        {
          reference: 'C1',
          explanation: 'Explanation 1',
          materialEffect: true,
        },
        {
          reference: 'C2',
          explanation: 'Explanation 2',
          materialEffect: false,
        },
      ],
    };
    component.isEditable = true;
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonCompliancesGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true, with data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(NonCompliancesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-compliances-group`, `change`, `delete` and `add` buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual(
        'Non-compliances that were not corrected before completing this report',
      );

      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
        'Yes',
        'Change',
      ]);
      expect(page.summaryTable).toEqual([
        'C1',
        'Explanation 1',
        'Material',
        'Change',
        'Remove',
        'C2',
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
      fixture = TestBed.createComponent(NonCompliancesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.uncorrectedNonCompliances.areThereUncorrectedNonCompliances = false;
      component.uncorrectedNonCompliances.uncorrectedNonCompliances = null;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-compliances-group`, `change`, `delete` and add buttons', () => {
      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
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
      fixture = TestBed.createComponent(NonCompliancesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `non-compliances-group` and not render delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual(
        'Non-compliances that were not corrected before completing this report',
      );
      expect(page.summaryListGuardQuestion).toEqual([
        'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
        'Yes',
      ]);
      expect(page.summaryTable).toEqual([
        'C1',
        'Explanation 1',
        'Material',
        '',
        '',
        'C2',
        'Explanation 2',
        'Immaterial',
        '',
        '',
      ]);
      expect(page.addButton).toBeFalsy();
    });
  });
});
