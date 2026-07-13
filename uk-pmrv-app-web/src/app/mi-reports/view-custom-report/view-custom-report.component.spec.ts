import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EMPTY, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'pmrv-api';

import { manipulateResultsAndExportToExcel } from '../core/mi-report';
import { buildGenerateReportError } from '../errors/business-error';
import { ViewCustomReportComponent } from './view-custom-report.component';

jest.mock('../core/mi-report', () => ({
  ...jest.requireActual('../core/mi-report'),
  manipulateResultsAndExportToExcel: jest.fn(),
}));

describe('ViewCustomReportComponent', () => {
  let component: ViewCustomReportComponent;
  let fixture: ComponentFixture<ViewCustomReportComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let businessErrorService: Partial<jest.Mocked<BusinessErrorService>>;

  const report: MiReportUserDefinedDTO = {
    reportName: 'Active installations by permit type and region',
    description: 'Returns all active installation accounts grouped by permit type and region.',
    queryDefinition: 'SELECT * FROM accounts',
    categories: [
      { id: 1, name: 'Workflow Submission Status' },
      { id: 2, name: 'Management' },
    ],
    lastUpdatedOn: '2026-01-26T10:00:00Z',
  };

  const generatedResult = { columnNames: ['account_id'], results: [{ account_id: 1 }] };

  class Page extends BasePage<ViewCustomReportComponent> {
    get heading(): HTMLElement {
      return this.query('app-page-heading');
    }

    get editReportButton(): HTMLAnchorElement {
      return this.query('a.edit-report');
    }

    get categories(): string[] {
      return this.queryAll<HTMLLIElement>('dd li').map((li) => li.textContent.trim());
    }

    get summaryValues(): string[] {
      return this.queryAll<HTMLElement>('dd').map((dd) => dd.textContent.replace(/\s+/g, ' ').trim());
    }

    get sqlTextarea(): HTMLTextAreaElement {
      return this.query('textarea');
    }

    get errorSummary(): HTMLElement {
      return this.query('govuk-error-summary');
    }

    submitExport(): void {
      this.query('form').dispatchEvent(new Event('submit'));
      this.fixture.detectChanges();
    }
  }

  const routeStub = new ActivatedRouteStub({ id: '7' }, null, { report });

  beforeEach(async () => {
    (manipulateResultsAndExportToExcel as jest.Mock).mockClear();

    miReportsService = {
      generateCustomReport: jest.fn().mockReturnValue(of(generatedResult)),
    };

    businessErrorService = {
      showError: jest.fn().mockReturnValue(EMPTY),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: MiReportsUserDefinedService, useValue: miReportsService },
        { provide: BusinessErrorService, useValue: businessErrorService },
      ],
      declarations: [ViewCustomReportComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewCustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the report name with an edit report button alongside it', () => {
    expect(page.heading.textContent).toContain(report.reportName);
    expect(page.editReportButton).toBeTruthy();
    expect(page.editReportButton.textContent.trim()).toBe('Edit report');
  });

  it('should display the categories as a list, plus the description and last updated date', () => {
    expect(page.categories).toEqual(['Workflow Submission Status', 'Management']);

    const [, description, lastUpdated] = page.summaryValues;
    expect(description).toBe(report.description);
    expect(lastUpdated).toBe('26 Jan 2026');
  });

  it('should prefill the editable SQL query with the saved query definition', () => {
    expect(page.sqlTextarea.value).toBe(report.queryDefinition);
  });

  it('should export using the query currently shown, including temporary edits', () => {
    component.form.setValue({ sqlQuery: 'SELECT 1' });
    fixture.detectChanges();

    page.submitExport();

    expect(miReportsService.generateCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT 1' });
    expect(manipulateResultsAndExportToExcel).toHaveBeenCalledWith(generatedResult, report.reportName);
  });

  it('should trim the query before exporting', () => {
    component.form.setValue({ sqlQuery: '  SELECT 1  ' });
    fixture.detectChanges();

    page.submitExport();

    expect(miReportsService.generateCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT 1' });
  });

  it('should show the error summary and not export when the query is empty', () => {
    component.form.setValue({ sqlQuery: '' });
    fixture.detectChanges();

    page.submitExport();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.generateCustomReport).not.toHaveBeenCalled();
    expect(manipulateResultsAndExportToExcel).not.toHaveBeenCalled();
  });

  it('should show the error summary and not export when the query is only whitespace', () => {
    component.form.setValue({ sqlQuery: '   \n  ' });
    fixture.detectChanges();

    page.submitExport();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.generateCustomReport).not.toHaveBeenCalled();
    expect(manipulateResultsAndExportToExcel).not.toHaveBeenCalled();
  });

  it('should broadcast a business error and not export when the query cannot be validated', () => {
    const message = 'The SQL query could not be validated';
    miReportsService.generateCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'MIREPORT1002', message } })),
    );

    page.submitExport();

    expect(businessErrorService.showError).toHaveBeenCalledWith(buildGenerateReportError(message, 7));
    expect(manipulateResultsAndExportToExcel).not.toHaveBeenCalled();
  });
});
