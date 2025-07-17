import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { BasePage } from '@testing';

import { AirOperatorFollowupItemComponent } from './air-operator-followup-item.component';

describe('AirOperatorFollowupItemComponent', () => {
  let page: Page;
  let component: AirOperatorFollowupItemComponent;
  let fixture: ComponentFixture<AirOperatorFollowupItemComponent>;

  class Page extends BasePage<AirOperatorFollowupItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirOperatorFollowupItemComponent],
      imports: [AirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirOperatorFollowupItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.reference = '1';
    component.attachedFiles = [
      {
        downloadUrl: '/downloads/111111',
        fileName: '100.png',
      },
      {
        downloadUrl: '/downloads/222222',
        fileName: '200.png',
      },
    ];
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    component.operatorAirImprovementFollowUpResponse = {
      improvementCompleted: true,
      dateCompleted: '2022-01-01',
      reason: 'Test reason 2',
      files: ['111111', '222222'],
    };
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Your response',
      'Is the required improvement complete?',
      'Yes',
      'Change',
      'When was the improvement completed?',
      '1 Jan 2022',
      'Change',
      'Tell us about your response to this improvement',
      'Test reason 2',
      'Change',
      'Uploaded files',
      '100.png  200.png',
      'Change',
    ]);
  });
});
