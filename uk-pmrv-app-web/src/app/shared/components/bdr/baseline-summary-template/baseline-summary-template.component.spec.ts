import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';

import { BDR } from 'pmrv-api';

import { BaselineSummaryTemplateComponent } from './baseline-summary-template.component';

describe('BaselineSummaryTemplateComponent', () => {
  let page: Page;
  let component: BaselineSummaryTemplateComponent;
  let fixture: ComponentFixture<BaselineSummaryTemplateComponent>;

  class Page extends BasePage<BaselineSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        TaskSharedModule,
        BdrTaskSharedModule,
        RouterTestingModule,
        BaselineSummaryTemplateComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BaselineSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.files = [
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

  it('should show all html elements ', () => {
    component.data = {
      isApplicationForFreeAllocation: false,
      statusApplicationType: 'HSE',
      infoIsCorrectChecked: true,
      files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
    } as BDR;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Uploaded supporting files',
      '100.png  200.png',
      'Change',
      'Are you applying for free allocation?',
      'No',
      'Change',
      'Are you applying for HSE or USE status?',
      'Yes, I am applying for HSE status',
      'Change',
      'I confirm that the information provided in this submission is correct to the best of my knowledge.',
      'Yes',
      'Change',
    ]);
  });
});
