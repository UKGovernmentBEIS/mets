import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EMPTY, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService } from 'pmrv-api';

import { buildCustomReportError } from '../errors/business-error';
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

    submitForm(): void {
      this.query('form').dispatchEvent(new Event('submit'));
      this.fixture.detectChanges();
    }
  }

  const routeStub = new ActivatedRouteStub();

  beforeEach(async () => {
    miReportsService = {
      getCategories: jest.fn().mockReturnValue(of(categories)),
      createCustomReport: jest.fn().mockReturnValue(of({})),
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
      declarations: [AddCustomReportComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
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

    expect(miReportsService.createCustomReport).toHaveBeenCalledWith({
      reportName: 'Active installations',
      description: 'A useful report',
      queryDefinition: 'SELECT * FROM accounts',
      categories: [{ id: 2 }, { id: 4 }],
    });
    expect(router.navigate).toHaveBeenCalledWith(['../'], {
      relativeTo: routeStub,
      state: { notification: true },
    });
  });

  it('should broadcast the response message as a business error and not navigate when the report name already exists', () => {
    const message = 'A report with this name already exists';
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

    expect(businessErrorService.showError).toHaveBeenCalledWith(buildCustomReportError(message));
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
});
