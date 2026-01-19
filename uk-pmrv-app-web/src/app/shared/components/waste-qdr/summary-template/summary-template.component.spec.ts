import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { WasteQdrReturnLinkComponent, WasteQdrTaskComponent } from '@tasks/waste-qdr/shared/components';
import { BasePage } from '@testing';

import { WasteQDR } from 'pmrv-api';

import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let page: Page;
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<SummaryTemplateComponent>;

  class Page extends BasePage<SummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        TaskSharedModule,
        WasteQdrReturnLinkComponent,
        WasteQdrTaskComponent,
        RouterTestingModule,
        SummaryTemplateComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.qdrReport = {
      downloadUrl: '/downloads/111111',
      fileName: '100.png',
    };
    component.reportProvided = true;
    component.supportingFiles = [
      {
        downloadUrl: '/downloads/111111',
        fileName: '100.png',
      },
      {
        downloadUrl: '/downloads/222222',
        fileName: '200.png',
      },
    ];
    component.quartelyTitle = 'Uploaded quartely report';
    component.notes = 'Some notes';
    component.reasonForUnprovided = 'Some reason for unprovided';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    component.data = {
      qdrReport: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
      supportingFiles: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
    } as WasteQDR;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Uploaded quartely report',
      'Yes',
      'Change report provided',
      'Completed quarterly report',
      '100.png',
      'Change waste qdr report',
      'Supporting data',
      '100.png  200.png',
      'Change supporting files',
      'Notes',
      'Some notes',
      'Change notes',
    ]);
  });
});
