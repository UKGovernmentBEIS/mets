import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { PermanentCessation } from 'pmrv-api';

import { PermanentCessationDetailsSummaryTemplateComponent } from './permanent-cessation-details-summary-template.component';

describe('PermanentCessationDetailsSummaryTemplateComponent', () => {
  let component: PermanentCessationDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<PermanentCessationDetailsSummaryTemplateComponent>;
  let page: Page;

  const mockPermanentCessationData: PermanentCessation = {
    description: 'Description',
    cessationScope: 'WHOLE_INSTALLATION',
    additionalDetails: 'Additional details',
    cessationDate: '2025-03-18',
    regulatorComments: 'Regulator comments',
    files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
  };

  class Page extends BasePage<PermanentCessationDetailsSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermanentCessationDetailsSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PermanentCessationDetailsSummaryTemplateComponent);
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
    component.data = mockPermanentCessationData;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Describe the cessation of regulated activities',
      'Description',
      'Change',
      'Supporting documents',
      '100.png  200.png',
      'Change',
      'Date of cessation',
      '18 Mar 2025',
      'Change',
      'Scope of the cessation',
      'Whole installation, including any sub-installations',
      'Change',
      'Additional details to be included in the notice document',
      'Additional details',
      'Change',
      'Regulator comments (optional)',
      'Regulator comments',
      'Change',
    ]);
  });
});
