import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { HSETI } from 'pmrv-api';

import { DetailsSummaryTemplateComponent } from './details-summary-template.component';

describe('DetailsSummaryTemplateComponent', () => {
  let page: Page;
  let component: DetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<DetailsSummaryTemplateComponent>;

  class Page extends BasePage<DetailsSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsSummaryTemplateComponent);
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
    component.hsetiFile = {
      downloadUrl: '/downloads/111111',
      fileName: 'HSETI.png',
    };
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    component.data = {
      hsetiFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
      files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
      notes: 'Notes text',
    } as HSETI;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Uploaded HSE target increase file',
      'HSETI.png',
      'Change uploaded HSE target increase file',
      'Uploaded supporting files',
      '100.png  200.png',
      'Change uploaded supporting files',
      'Notes',
      'Notes text',
      'Change notes',
    ]);
  });
});
