import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RecommendedImprovementsGroupComponent } from '@shared/components/review-groups/recommended-improvements-group/recommended-improvements-group.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('RecommendedImprovementsGroupComponent', () => {
  let page: Page;
  let component: RecommendedImprovementsGroupComponent;
  let fixture: ComponentFixture<RecommendedImprovementsGroupComponent>;

  class Page extends BasePage<RecommendedImprovementsGroupComponent> {
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
    component.recommendedImprovements = {
      areThereRecommendedImprovements: true,
      recommendedImprovements: [
        {
          reference: 'D1',
          explanation: 'Explanation 1',
        },
        {
          reference: 'D2',
          explanation: 'Explanation 2',
        },
      ],
    };
    component.isEditable = true;
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecommendedImprovementsGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true, with data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(RecommendedImprovementsGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `recommended-improvements-group`, `change`, `delete` and `add` buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Recommended improvements');

      expect(page.summaryListGuardQuestion).toEqual(['Are there any recommended improvements?', 'Yes', 'Change']);
      expect(page.summaryTable).toEqual([
        'D1',
        'Explanation 1',
        'Change',
        'Remove',
        'D2',
        'Explanation 2',
        'Change',
        'Remove',
      ]);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is true, without data', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(RecommendedImprovementsGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.recommendedImprovements.areThereRecommendedImprovements = false;
      component.recommendedImprovements.recommendedImprovements = null;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `recommended-improvements-group`, `change`, `delete` and add buttons', () => {
      expect(page.summaryListGuardQuestion).toEqual(['Are there any recommended improvements?', 'No', 'Change']);
      expect(page.summaryTable).toEqual([]);
      expect(page.heading2).toBeTruthy();
      expect(page.addButton).toBeFalsy();
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(RecommendedImprovementsGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the `recommended-improvements-group` and not render delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Recommended improvements');
      expect(page.summaryListGuardQuestion).toEqual(['Are there any recommended improvements?', 'Yes']);
      expect(page.summaryTable).toEqual(['D1', 'Explanation 1', '', '', 'D2', 'Explanation 2', '', '']);
      expect(page.addButton).toBeFalsy();
    });
  });
});
