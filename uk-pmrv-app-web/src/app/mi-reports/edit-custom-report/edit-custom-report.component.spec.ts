import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EMPTY, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'pmrv-api';

import { buildEditCustomReportError } from '../errors/business-error';
import { CustomReportPreviewComponent } from '../shared/custom-report-preview/custom-report-preview.component';
import { EditCustomReportComponent } from './edit-custom-report.component';

describe('EditCustomReportComponent', () => {
  let component: EditCustomReportComponent;
  let fixture: ComponentFixture<EditCustomReportComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let businessErrorService: Partial<jest.Mocked<BusinessErrorService>>;
  let router: Router;

  const categories = [
    { id: 2, name: 'Category A' },
    { id: 4, name: 'Category B' },
  ];

  const report: MiReportUserDefinedDTO = {
    reportName: 'Active installations by permit type and region',
    description: 'Returns all active installation accounts grouped by permit type and region.',
    queryDefinition: 'SELECT * FROM accounts',
    categories: [{ id: 2, name: 'Category A' }],
    lastUpdatedOn: '2026-01-26T10:00:00Z',
  };

  class Page extends BasePage<EditCustomReportComponent> {
    get heading(): HTMLElement {
      return this.query('app-page-heading');
    }

    get errorSummary(): HTMLElement {
      return this.query('govuk-error-summary');
    }

    get cancelLink(): HTMLAnchorElement {
      return this.query('a.cancel-edit');
    }

    get previewButton(): HTMLButtonElement {
      return this.queryAll<HTMLButtonElement>('button').find((b) => b.textContent.trim() === 'Preview results');
    }

    get previewHeaders(): string[] {
      return this.queryAll<HTMLElement>('.govuk-table__head th').map((th) => th.textContent.trim());
    }

    get previewCells(): string[] {
      return this.queryAll<HTMLElement>('.govuk-table__body td').map((td) => td.textContent.trim());
    }

    submitForm(): void {
      this.query('form').dispatchEvent(new Event('submit'));
      this.fixture.detectChanges();
    }

    clickPreview(): void {
      this.previewButton.click();
      this.fixture.detectChanges();
    }
  }

  const routeStub = new ActivatedRouteStub({ id: '7' }, null, { report });

  beforeEach(async () => {
    miReportsService = {
      getCategories: jest.fn().mockReturnValue(of(categories)),
      updateCustomReport: jest.fn().mockReturnValue(of({})),
      previewCustomReport: jest.fn().mockReturnValue(of({ columnNames: [], results: [] })),
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
      declarations: [EditCustomReportComponent, CustomReportPreviewComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditCustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the report name as the heading caption with a cancel link back to the report', () => {
    expect(page.heading.textContent).toContain(report.reportName);
    expect(page.heading.textContent).toContain('Edit report');
    expect(page.cancelLink).toBeTruthy();
    expect(page.cancelLink.textContent.trim()).toBe('Cancel');
  });

  it('should prefill the form with the saved report and an empty reason for change', () => {
    expect(component.form.value).toEqual({
      reportName: report.reportName,
      categories: ['2'],
      description: report.description,
      queryDefinition: report.queryDefinition,
      reasonForChange: null,
    });
  });

  it('should show the error summary and not save when the reason for change is missing', () => {
    page.submitForm();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('Enter a reason for change');
    expect(miReportsService.updateCustomReport).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should update the report and navigate back to the report view on submit', () => {
    component.form.patchValue({
      reportName: 'Active installations',
      categories: ['2', '4'],
      description: 'A useful report',
      queryDefinition: 'SELECT 1',
      reasonForChange: 'Updated the query to include new fields',
    });

    page.submitForm();

    expect(miReportsService.updateCustomReport).toHaveBeenCalledWith(7, {
      userDefinedDTO: {
        reportName: 'Active installations',
        description: 'A useful report',
        queryDefinition: 'SELECT 1',
        categories: [{ id: 2 }, { id: 4 }],
      },
      reasonForChange: 'Updated the query to include new fields',
    });
    expect(router.navigate).toHaveBeenCalledWith(['../../view-custom-report', 7], {
      relativeTo: routeStub,
      state: { notification: 'The report has been saved' },
    });
  });

  it('should show a specific form error on the SQL query field when the query is invalid', () => {
    const message = 'Only SELECT statements are allowed';
    miReportsService.updateCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'FORM1001', message } })),
    );

    component.form.patchValue({ queryDefinition: 'DROP TABLE accounts', reasonForChange: 'A reason' });

    page.submitForm();

    expect(component.form.get('queryDefinition').errors).toEqual({ invalidSqlQuery: 'Enter a valid SQL query' });
    expect(page.errorSummary).toBeTruthy();
    expect(businessErrorService.showError).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should show a specific form error on the report name field when the report name already exists', () => {
    const message = 'The provided MI Report name already exists';
    miReportsService.updateCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { code: 'MIREPORT1001', message } })),
    );

    component.form.patchValue({ reportName: 'Existing report', reasonForChange: 'A reason' });

    page.submitForm();

    expect(component.form.get('reportName').errors).toEqual({
      reportNameExists: 'The report name already exists. Enter a different report name.',
    });
    expect(page.errorSummary).toBeTruthy();
    expect(businessErrorService.showError).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should broadcast the response message as a business error and not navigate when the query cannot be validated', () => {
    const message = 'The SQL query could not be validated';
    miReportsService.updateCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { code: 'MIREPORT1002', message } })),
    );

    component.form.patchValue({ reasonForChange: 'A reason' });

    page.submitForm();

    expect(businessErrorService.showError).toHaveBeenCalledWith(buildEditCustomReportError(message, 7));
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should preview the first results of the currently entered query as a table with dynamic columns', () => {
    miReportsService.previewCustomReport.mockReturnValue(
      of({ columnNames: ['account_id'], results: [{ account_id: 'UK-E-IN-00001' }] }),
    );

    page.clickPreview();

    expect(miReportsService.previewCustomReport).toHaveBeenCalledWith({ sqlQuery: report.queryDefinition });
    expect(page.previewHeaders).toEqual(['account_id']);
    expect(page.previewCells).toEqual(['UK-E-IN-00001']);
  });

  it('should show the error summary and not preview when the query is empty', () => {
    component.form.get('queryDefinition').setValue('');

    page.clickPreview();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.previewCustomReport).not.toHaveBeenCalled();
  });

  it('should show the error on the query field and not display a preview when the previewed query is invalid', () => {
    const message = 'Custom query could not be executed';
    miReportsService.previewCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'REPORT1001', message } })),
    );

    page.clickPreview();

    expect(component.form.get('queryDefinition').errors).toEqual({ invalidSqlQuery: message });
    expect(page.errorSummary).toBeTruthy();
    expect(page.previewHeaders).toEqual([]);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
