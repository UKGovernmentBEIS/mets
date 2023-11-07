import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { VirSharedModule } from '../../vir-shared.module';
import { OperatorResponseItemComponent } from './operator-response-item.component';

describe('OperatorResponseItemComponent', () => {
  let page: Page;
  let component: OperatorResponseItemComponent;
  let fixture: ComponentFixture<OperatorResponseItemComponent>;

  class Page extends BasePage<OperatorResponseItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorResponseItemComponent],
      imports: [VirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(OperatorResponseItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.reference = 'A1';
    component.operatorImprovementResponse = {
      isAddressed: true,
      addressedDescription: 'Test description',
      addressedDate: '2040-06-03',
      uploadEvidence: true,
      files: ['111111', '222222', '333333'],
    };
    component.attachedFiles = [
      {
        downloadUrl: '/downloads/111111',
        fileName: '100.png',
      },
      {
        downloadUrl: '/downloads/222222',
        fileName: '200.png',
      },
      {
        downloadUrl: '/downloads/333333',
        fileName: '300.png',
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Your response',
      'Addressed?',
      'Yes - has been addressed or will be in the future',
      'Change',
      'Operator response',
      'Test description',
      'Change',
      'Date of improvement',
      '3 Jun 2040',
      'Change',
      'Evidence uploaded?',
      'Yes',
      'Change',
      'Uploaded files',
      '100.png200.png300.png',
      'Change',
    ]);
  });
});
