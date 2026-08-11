import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { CustomReportPreviewComponent } from './custom-report-preview.component';

describe('CustomReportPreviewComponent', () => {
  let component: CustomReportPreviewComponent;
  let fixture: ComponentFixture<CustomReportPreviewComponent>;
  let page: Page;

  class Page extends BasePage<CustomReportPreviewComponent> {
    get heading(): HTMLElement {
      return this.query('h2');
    }

    get headers(): string[] {
      return this.queryAll<HTMLElement>('.govuk-table__head th').map((th) => th.textContent.trim());
    }

    get cells(): string[] {
      return this.queryAll<HTMLElement>('.govuk-table__body td').map((td) => td.textContent.trim());
    }

    get emptyMessage(): HTMLElement {
      return this.query('p.govuk-body');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [CustomReportPreviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomReportPreviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  });

  it('should not display anything when there is no preview', () => {
    fixture.detectChanges();

    expect(page.heading).toBeFalsy();
  });

  it('should display the preview as a table with the given columns and rows', () => {
    component.preview = {
      columns: [
        { field: 'account_id', header: 'account_id' },
        { field: 'installation_name', header: 'installation_name' },
      ],
      rows: [{ account_id: 'UK-E-IN-00001', installation_name: 'Wentworth Energy Ltd' }],
    };
    fixture.detectChanges();

    expect(page.heading.textContent.trim()).toBe('Preview of the first 10 results');
    expect(page.headers).toEqual(['account_id', 'installation_name']);
    expect(page.cells).toEqual(['UK-E-IN-00001', 'Wentworth Energy Ltd']);
  });

  it('should display a message when the preview has no columns', () => {
    component.preview = { columns: [], rows: [] };
    fixture.detectChanges();

    expect(page.emptyMessage).toBeTruthy();
    expect(page.emptyMessage.textContent.trim()).toBe('The query returned no results.');
  });
});
