import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AlrDeterminationSummaryTemplateComponent } from './determination-summary-template.component';

describe('AlrDeterminationSummaryTemplateComponent', () => {
  let page: Page;
  let component: AlrDeterminationSummaryTemplateComponent;
  let fixture: ComponentFixture<AlrDeterminationSummaryTemplateComponent>;

  class Page extends BasePage<AlrDeterminationSummaryTemplateComponent> {
    get pageContents() {
      return Array.from(this.query<HTMLDivElement>('dl').querySelectorAll('dt, dd')).map((item) =>
        item.textContent.trim(),
      );
    }

    get appAlrDeterminationCloseSummaryTemplate() {
      return this.query('app-alr-determination-close-summary-template');
    }

    get appAlrDeterminationProceedAuthoritySummaryTemplate() {
      return this.query('app-alr-determination-proceed-authority-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(AlrDeterminationSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.editable = true;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements and appAlrDeterminationCloseSummaryTemplate', () => {
    component.determination = {
      type: 'CLOSED_ALR',
      reason: 'Official notice',
      alrFile: '',
    };
    fixture.detectChanges();
    expect(page.pageContents).toEqual(['Decision', 'Close task', 'Change']);
    expect(page.appAlrDeterminationCloseSummaryTemplate).toBeTruthy();
    expect(page.appAlrDeterminationProceedAuthoritySummaryTemplate).toBeFalsy();
  });

  it('should show all html elements and appAlrDeterminationCloseSummaryTemplate', () => {
    component.determination = {
      type: 'PROCEED_TO_AUTHORITY',
      reason: 'Official notice',
      alrFile: '',
    };
    fixture.detectChanges();
    expect(page.pageContents).toEqual(['Decision', 'Proceed to UK ETS authority', 'Change']);
    expect(page.appAlrDeterminationCloseSummaryTemplate).toBeFalsy();
    expect(page.appAlrDeterminationProceedAuthoritySummaryTemplate).toBeTruthy();
  });
});
