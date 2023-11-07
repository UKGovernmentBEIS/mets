import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { BasePage } from '@testing';

import { AirRegulatorResponseItemComponent } from './air-regulator-response-item.component';

describe('AirRegulatorResponseItemComponent', () => {
  let page: Page;
  let component: AirRegulatorResponseItemComponent;
  let fixture: ComponentFixture<AirRegulatorResponseItemComponent>;

  class Page extends BasePage<AirRegulatorResponseItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirRegulatorResponseItemComponent],
      imports: [AirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirRegulatorResponseItemComponent);
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
    component.regulatorAirImprovementResponse = {
      improvementRequired: true,
      improvementDeadline: '2040-01-01',
      officialResponse: 'Test official response 1',
      comments: 'Test comment 1',
      files: ['111111', '222222'],
    };
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Your response',
      'Is the improvement required?',
      'Yes',
      'Change',
      'Completion date',
      '1 Jan 2040',
      'Change',
      'Text for improvement response letter',
      'Test official response 1',
      'Change',
      'Comments',
      'Test comment 1',
      'Change',
      'Uploaded files',
      '100.png200.png',
      'Change',
    ]);
  });
});
