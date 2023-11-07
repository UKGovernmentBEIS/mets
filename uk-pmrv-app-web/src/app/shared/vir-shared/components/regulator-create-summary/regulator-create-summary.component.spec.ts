import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { BasePage } from '@testing';

import { RegulatorCreateSummaryComponent } from './regulator-create-summary.component';

describe('RegulatorCreateSummaryComponent', () => {
  let page: Page;
  let component: RegulatorCreateSummaryComponent;
  let fixture: ComponentFixture<RegulatorCreateSummaryComponent>;

  class Page extends BasePage<RegulatorCreateSummaryComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegulatorCreateSummaryComponent],
      imports: [VirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(RegulatorCreateSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.regulatorReviewResponse = {
      reportSummary: 'Test summary',
      regulatorImprovementResponses: {},
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual(['Create summary', 'Report summary', 'Test summary', 'Change']);
  });
});
