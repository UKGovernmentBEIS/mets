import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { BasePage } from '@testing';

import { RegulatorResponseItemComponent } from './regulator-response-item.component';

describe('RegulatorResponseItemComponent', () => {
  let page: Page;
  let component: RegulatorResponseItemComponent;
  let fixture: ComponentFixture<RegulatorResponseItemComponent>;

  class Page extends BasePage<RegulatorResponseItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegulatorResponseItemComponent],
      imports: [VirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(RegulatorResponseItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.regulatorImprovementResponse = {
      improvementRequired: true,
      improvementComments: 'Test comments',
      operatorActions: 'Test operator actions',
      improvementDeadline: '2040-06-03',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Your response',
      "Regulator's decision",
      'Improvement is required',
      'Change',
      "Regulator's response",
      'Test comments',
      'Change',
      'Actions for the operator',
      'Test operator actions',
      'Change',
      'Deadline for improvement',
      '3 Jun 2040',
      'Change',
    ]);
  });
});
