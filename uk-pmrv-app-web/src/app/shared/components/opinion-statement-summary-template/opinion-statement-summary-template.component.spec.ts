import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { OpinionStatementSummaryTemplateComponent } from './opinion-statement-summary-template.component';

describe('OpinionStatementSummaryTemplateComponent', () => {
  let page: Page;
  let component: OpinionStatementSummaryTemplateComponent;
  let fixture: ComponentFixture<OpinionStatementSummaryTemplateComponent>;

  class Page extends BasePage<OpinionStatementSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OpinionStatementSummaryTemplateComponent],
      imports: [SharedModule],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(OpinionStatementSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.notes = 'Test notes';
    component.opinionStatementFiles = [
      {
        downloadUrl: '/downloads/111111',
        fileName: '100.png',
      },
      {
        downloadUrl: '/downloads/222222',
        fileName: '200.png',
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'Uploaded BDR verification opinion statement',
      '100.png  200.png',
      'Change',
      'Notes',
      'Test notes',
      'Change',
    ]);
  });
});
