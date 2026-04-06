import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { NerDetailsSummaryTemplateComponent } from './details-summary-template.component';

describe('DetailsSummaryTemplateComponent', () => {
  let component: NerDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<NerDetailsSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<NerDetailsSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  const initiateProperties = () => {
    component.isEditable = true;
    component.nerFile = { downloadUrl: '/tasks/1/file-download/', fileName: 'Test1.txt' };
    component.nerSupportingFiles = [{ downloadUrl: '/tasks/1/file-download/', fileName: 'Test2.txt' }];
    component.mmpFile = { downloadUrl: '/tasks/1/file-download/', fileName: 'Test3.txt' };
    component.mmpSupportingFiles = [{ downloadUrl: '/tasks/1/file-download/', fileName: 'Test4.txt' }];
    component.ner = {
      nerFiles: {
        file: '22222222-2222-4222-a222-222222222222',
        supportingFiles: ['11111111-1111-4111-a111-111111111111'],
      },
      mmpFiles: {
        file: '33333333-3333-4222-a222-333333333333',
        supportingFiles: ['44444444-4444-4111-a111-444444444444'],
      },
      notes: 'A note',
    };
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerDetailsSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(NerDetailsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    initiateProperties();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'Uploaded new entrant reserve',
      'Test1.txt',
      'Change  uploaded new entrant reserve file',
      'Uploaded supporting files',
      'Test2.txt',
      'Change  uploaded supporting files for new entrant reserve',
      'Notes',
      'A note',
      'Change  notes for new entrant reserve',
      'Uploaded monitoring methodology plan',
      'Test3.txt',
      'Change  uploaded monitoring methodology plan file',
      'Uploaded supporting files',
      'Test4.txt',
      'Change  uploaded supporting files for monitoring methodology plan',
    ]);
  });
});
