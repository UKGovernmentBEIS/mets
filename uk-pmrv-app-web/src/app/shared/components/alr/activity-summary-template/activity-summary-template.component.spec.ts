import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { ALRReturnLinkComponent, AlrTaskComponent } from '@tasks/alr/shared/components';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';

import { ALR } from 'pmrv-api';

import { ActivitySummaryTemplateComponent } from './activity-summary-template.component';

describe('ActivitySummaryTemplateComponent', () => {
  let page: Page;
  let component: ActivitySummaryTemplateComponent;
  let fixture: ComponentFixture<ActivitySummaryTemplateComponent>;

  class Page extends BasePage<ActivitySummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        TaskSharedModule,
        AlrTaskComponent,
        ALRReturnLinkComponent,
        RouterTestingModule,
        ActivitySummaryTemplateComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivitySummaryTemplateComponent);
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
    component.alrFile = {
      downloadUrl: '/downloads/111111',
      fileName: 'ALR.png',
    };
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    component.data = {
      alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
      files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
    } as ALR;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Uploaded activity level report',
      'ALR.png',
      'Change',
      'Uploaded supporting files',
      '100.png  200.png',
      'Change',
    ]);
  });
});
