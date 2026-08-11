import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EMPTY, of, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService } from 'pmrv-api';

import { buildCustomReportError } from '../errors/business-error';
import { CustomReportPreviewComponent } from '../shared/custom-report-preview/custom-report-preview.component';
import { AddCustomReportComponent } from './add-custom-report.component';

describe('AddCustomReportComponent', () => {
  let component: AddCustomReportComponent;
  let fixture: ComponentFixture<AddCustomReportComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let businessErrorService: Partial<jest.Mocked<BusinessErrorService>>;
  let router: Router;

  const categories = [
    { id: 2, name: 'Category A' },
    { id: 4, name: 'Category B' },
  ];

  class Page extends BasePage<AddCustomReportComponent> {
    get errorSummary(): HTMLElement {
      return this.query('govuk-error-summary');
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

    get previewEmptyMessage(): HTMLElement {
      return this.query('app-custom-report-preview p.govuk-body');
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

  const routeStub = new ActivatedRouteStub();

  beforeEach(async () => {
    miReportsService = {
      getCategories: jest.fn().mockReturnValue(of(categories)),
      createCustomReport: jest.fn().mockReturnValue(of({})),
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
      declarations: [AddCustomReportComponent, CustomReportPreviewComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    const authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the error summary and not navigate when submitting an empty form', () => {
    page.submitForm();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.createCustomReport).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should create the report and navigate back to the list on submit', () => {
    component.form.patchValue({
      reportName: 'Active installations',
      categories: ['2', '4'],
      description: 'A useful report',
      queryDefinition: 'SELECT * FROM accounts',
    });

    page.submitForm();

    expect(miReportsService.createCustomReport).toHaveBeenCalledWith('INSTALLATION', {
      reportName: 'Active installations',
      description: 'A useful report',
      queryDefinition: 'SELECT * FROM accounts',
      categories: [{ id: 2 }, { id: 4 }],
    });
    expect(router.navigate).toHaveBeenCalledWith(['../'], {
      relativeTo: routeStub,
      state: { notification: 'Report saved' },
    });
  });

  it('should show a specific form error on the SQL query field when the query is invalid', () => {
    const message = 'Only SELECT statements are allowed';
    miReportsService.createCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'FORM1001', message } })),
    );

    component.form.patchValue({
      reportName: 'Active installations',
      categories: ['2', '4'],
      description: 'A useful report',
      queryDefinition: 'DROP TABLE accounts',
    });

    page.submitForm();

    expect(component.form.get('queryDefinition').errors).toEqual({ invalidSqlQuery: 'Enter a valid SQL query' });
    expect(page.errorSummary).toBeTruthy();
    expect(businessErrorService.showError).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should show a specific form error on the report name field when the report name already exists', () => {
    const message = 'The provided MI Report name already exists';
    miReportsService.createCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { code: 'MIREPORT1001', message } })),
    );

    component.form.patchValue({
      reportName: 'Active installations',
      categories: ['2', '4'],
      description: 'A useful report',
      queryDefinition: 'SELECT * FROM accounts',
    });

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
    miReportsService.createCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { code: 'MIREPORT1002', message } })),
    );

    component.form.patchValue({
      reportName: 'Active installations',
      categories: ['2', '4'],
      description: 'A useful report',
      queryDefinition: 'SELECT * FROM accounts',
    });

    page.submitForm();

    expect(businessErrorService.showError).toHaveBeenCalledWith(buildCustomReportError(message));
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should show the error summary and not preview when the query is empty', () => {
    page.clickPreview();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.previewCustomReport).not.toHaveBeenCalled();
  });

  it('should preview the first results as a table with dynamic columns', () => {
    miReportsService.previewCustomReport.mockReturnValue(
      of({
        columnNames: ['account_id', 'installation_name'],
        results: [
          { account_id: 'UK-E-IN-00001', installation_name: 'Wentworth Energy Ltd' },
          { account_id: 'UK-E-IN-00002', installation_name: 'Agric Limited - Test' },
        ],
      }),
    );

    component.form.get('queryDefinition').setValue('SELECT * FROM accounts');
    page.clickPreview();

    expect(miReportsService.previewCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT * FROM accounts' });
    expect(page.previewHeaders).toEqual(['account_id', 'installation_name']);
    expect(page.previewCells).toEqual([
      'UK-E-IN-00001',
      'Wentworth Energy Ltd',
      'UK-E-IN-00002',
      'Agric Limited - Test',
    ]);
  });

  it('should show a message when the previewed query returns no results', () => {
    component.form.get('queryDefinition').setValue('SELECT * FROM accounts WHERE 1 = 0');
    page.clickPreview();

    expect(page.previewEmptyMessage).toBeTruthy();
    expect(page.previewEmptyMessage.textContent.trim()).toBe('The query returned no results.');
  });

  it('should show the error on the query field and not display a preview when the previewed query is invalid', () => {
    const message = 'Custom query could not be executed';
    miReportsService.previewCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'REPORT1001', message } })),
    );

    component.form.get('queryDefinition').setValue('SELEKT * FROM accounts');
    page.clickPreview();

    expect(component.form.get('queryDefinition').errors).toEqual({ invalidSqlQuery: message });
    expect(page.errorSummary).toBeTruthy();
    expect(page.previewHeaders).toEqual([]);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
