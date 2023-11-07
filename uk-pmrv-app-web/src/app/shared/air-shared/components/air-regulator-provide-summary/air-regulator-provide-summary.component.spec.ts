import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { BasePage } from '@testing';

import { AirRegulatorProvideSummaryComponent } from './air-regulator-provide-summary.component';

describe('AirRegulatorProvideSummaryComponent', () => {
  let page: Page;
  let component: AirRegulatorProvideSummaryComponent;
  let fixture: ComponentFixture<AirRegulatorProvideSummaryComponent>;

  class Page extends BasePage<AirRegulatorProvideSummaryComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirRegulatorProvideSummaryComponent],
      imports: [AirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirRegulatorProvideSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.regulatorAirReviewResponse = {
      reportSummary: 'Test summary',
      regulatorImprovementResponses: {},
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual(['Details', 'Text for official letter', 'Test summary', 'Change']);
  });
});
