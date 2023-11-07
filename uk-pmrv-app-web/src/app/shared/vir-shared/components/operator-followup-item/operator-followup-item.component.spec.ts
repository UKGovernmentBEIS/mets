import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { BasePage } from '@testing';

import { OperatorFollowupItemComponent } from './operator-followup-item.component';

describe('OperatorFollowupItemComponent', () => {
  let page: Page;
  let component: OperatorFollowupItemComponent;
  let fixture: ComponentFixture<OperatorFollowupItemComponent>;

  class Page extends BasePage<OperatorFollowupItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorFollowupItemComponent],
      imports: [VirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(OperatorFollowupItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.reference = 'A1';
    component.operatorImprovementFollowUpResponse = {
      improvementCompleted: true,
      dateCompleted: '2022-06-03',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Your response',
      'Improvement complete?',
      'Yes',
      'Change',
      'Date of improvement completion',
      '3 Jun 2022',
      'Change',
    ]);
  });
});
