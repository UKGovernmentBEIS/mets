import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DeterminationSummaryTemplateComponent } from './determination-summary-template.component';

describe('DeterminationSummaryTemplateComponent', () => {
  let page: Page;
  let component: DeterminationSummaryTemplateComponent;
  let fixture: ComponentFixture<DeterminationSummaryTemplateComponent>;

  class Page extends BasePage<DeterminationSummaryTemplateComponent> {
    get pageContents() {
      return Array.from(this.query<HTMLDivElement>('dl').querySelectorAll('dt, dd')).map((item) =>
        item.textContent.trim(),
      );
    }

    get appDoalDeterminationCloseSummaryTemplate() {
      return this.query('app-doal-determination-close-summary-template');
    }

    get appDoalDeterminationProceedAuthoritySummaryTemplate() {
      return this.query('app-doal-determination-proceed-authority-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeterminationSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DeterminationSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.editable = true;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements and appDoalDeterminationCloseSummaryTemplate', () => {
    component.determination = {
      type: 'CLOSED',
      reason: 'Official notice',
    };
    fixture.detectChanges();
    expect(page.pageContents).toEqual(['Decision', 'Close task', 'Change']);
    expect(page.appDoalDeterminationCloseSummaryTemplate).toBeTruthy();
    expect(page.appDoalDeterminationProceedAuthoritySummaryTemplate).toBeFalsy();
  });

  it('should show all html elements and appDoalDeterminationCloseSummaryTemplate', () => {
    component.determination = {
      type: 'PROCEED_TO_AUTHORITY',
      reason: 'Official notice',
    };
    fixture.detectChanges();
    expect(page.pageContents).toEqual(['Decision', 'Proceed to UK ETS authority', 'Change']);
    expect(page.appDoalDeterminationCloseSummaryTemplate).toBeFalsy();
    expect(page.appDoalDeterminationProceedAuthoritySummaryTemplate).toBeTruthy();
  });
});
