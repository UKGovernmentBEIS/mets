import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { OverallDecisionSummaryTemplateComponent } from './overall-decision-summary-template.component';

describe('OverallDecisionSummaryTemplateComponent', () => {
  let component: OverallDecisionSummaryTemplateComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<OverallDecisionSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OverallDecisionSummaryTemplateComponent],
      imports: [SharedModule],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(OverallDecisionSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.overallDecision = {
      type: 'VERIFIED_WITH_COMMENTS',
      reasons: 'Reason',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'Decision',
      'Verified as satisfactory with comments',
      'Change',
      'Your comments',
      'Reason',
      'Change',
    ]);
  });
});
