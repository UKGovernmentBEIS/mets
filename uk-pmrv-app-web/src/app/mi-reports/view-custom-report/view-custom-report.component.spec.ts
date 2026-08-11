import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EMPTY, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'pmrv-api';

import { manipulateResultsAndExportToExcel } from '../core/mi-report';
import { buildGenerateReportError } from '../errors/business-error';
import { CustomReportPreviewComponent } from '../shared/custom-report-preview/custom-report-preview.component';
import { ViewCustomReportComponent } from './view-custom-report.component';

jest.mock('../core/mi-report', () => ({
  ...jest.requireActual('../core/mi-report'),
  manipulateResultsAndExportToExcel: jest.fn(),
}));

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

describe('ViewCustomReportComponent', () => {
  let component: ViewCustomReportComponent;
  let fixture: ComponentFixture<ViewCustomReportComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let businessErrorService: Partial<jest.Mocked<BusinessErrorService>>;

  const generatedResult = { columnNames: ['account_id'], results: [{ account_id: 1 }] };

  class Page extends BasePage<ViewCustomReportComponent> {
    get heading(): HTMLElement {
      return this.query('app-page-heading');
    }

    get editReportButton(): HTMLAnchorElement {
      return this.query('a.edit-report');
    }

    get deleteReportButton(): HTMLAnchorElement {
      return this.query('a.delete-report');
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

    get viewReportHistoryLink(): HTMLAnchorElement {
      return this.query('a.view-report-history');
    }

    get favouriteToggleLink(): HTMLAnchorElement {
      return this.query('a.toggle-favourite');
    }

    toggleFavourite(): void {
      this.favouriteToggleLink.click();
      this.fixture.detectChanges();
    }

    get errorSummary(): HTMLElement {
      return this.query('govuk-error-summary');
    }

    get notificationBanner(): HTMLElement {
      return this.query('govuk-notification-banner');
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

    submitExport(): void {
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
    (manipulateResultsAndExportToExcel as jest.Mock).mockClear();

    miReportsService = {
      generateCustomReport: jest.fn().mockReturnValue(of(generatedResult)),
      previewCustomReport: jest.fn().mockReturnValue(of(generatedResult)),
      createFavourite: jest.fn().mockReturnValue(of(undefined)),
      deleteFavourite: jest.fn().mockReturnValue(of(undefined)),
      hasManageCustomReportsAccess: jest.fn().mockReturnValue(of(true)),
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
      declarations: [ViewCustomReportComponent, CustomReportPreviewComponent],
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

  it('should display the report name with edit and delete report buttons alongside it', () => {
    expect(page.heading.textContent).toContain(report.reportName);
    expect(page.editReportButton).toBeTruthy();
    expect(page.editReportButton.textContent.trim()).toBe('Edit report');
    expect(page.deleteReportButton).toBeTruthy();
    expect(page.deleteReportButton.textContent.trim()).toBe('Delete report');
  });

  it('should hide the edit and delete report buttons when the user lacks execute permission for custom reports', () => {
    miReportsService.hasManageCustomReportsAccess.mockReturnValue(of(false));

    const restrictedFixture = TestBed.createComponent(ViewCustomReportComponent);
    const restrictedPage = new Page(restrictedFixture);
    restrictedFixture.detectChanges();

    expect(restrictedPage.editReportButton).toBeFalsy();
    expect(restrictedPage.deleteReportButton).toBeFalsy();
  });

  it('should not display the saved notification banner by default', () => {
    expect(page.notificationBanner).toBeFalsy();
  });

  it('should display the saved notification banner when arriving from a successful save', () => {
    const router = TestBed.inject(Router);
    jest
      .spyOn(router, 'currentNavigation')
      .mockReturnValue({ extras: { state: { notification: 'The report has been saved' } } } as unknown as ReturnType<
        Router['currentNavigation']
      >);

    const savedFixture = TestBed.createComponent(ViewCustomReportComponent);
    savedFixture.detectChanges();

    const banner = savedFixture.nativeElement.querySelector('govuk-notification-banner');
    expect(banner).toBeTruthy();
    expect(banner.textContent).toContain('The report has been saved');
  });

  it('should display the categories as a list, plus the description and last updated date', () => {
    expect(page.categories).toEqual(['Workflow Submission Status', 'Management']);

    const [, description, lastUpdated] = page.summaryValues;
    expect(description).toBe(report.description);
    expect(lastUpdated).toBe('26 Jan 2026');
  });

  it('should display the add to favourites option beneath the report title', () => {
    expect(page.favouriteToggleLink).toBeTruthy();
    expect(page.favouriteToggleLink.textContent.trim()).toBe('Add to your favourites');
  });

  it('should mark the report as favourite and update the option when selecting add to favourites', () => {
    page.toggleFavourite();

    expect(miReportsService.createFavourite).toHaveBeenCalledWith(7);
    expect(page.favouriteToggleLink.textContent.trim()).toBe('Remove from your favourites');
  });

  it('should unmark the report and revert the option when selecting remove from favourites', () => {
    page.toggleFavourite();
    expect(page.favouriteToggleLink.textContent.trim()).toBe('Remove from your favourites');

    page.toggleFavourite();

    expect(miReportsService.deleteFavourite).toHaveBeenCalledWith(7);
    expect(page.favouriteToggleLink.textContent.trim()).toBe('Add to your favourites');
  });

  it('should display a link to the report history', () => {
    expect(page.viewReportHistoryLink).toBeTruthy();
    expect(page.viewReportHistoryLink.textContent.trim()).toBe('View report history');
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

  it('should show the error as a form error and not export when the edited query is invalid', () => {
    const message = 'Invalid SQL query';
    miReportsService.generateCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'REPORT1001', message } })),
    );

    component.form.setValue({ sqlQuery: 'SELECT * FROM nonexistent' });
    fixture.detectChanges();

    page.submitExport();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain(message);
    expect(component.form.controls.sqlQuery.errors).toEqual({ invalidSqlQuery: message });
    expect(businessErrorService.showError).not.toHaveBeenCalled();
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

  it('should preview the first results of the currently shown query as a table with dynamic columns', () => {
    miReportsService.previewCustomReport.mockReturnValue(
      of({ columnNames: ['account_id'], results: [{ account_id: 'UK-E-IN-00001' }] }),
    );

    component.form.setValue({ sqlQuery: 'SELECT account_id FROM accounts' });
    fixture.detectChanges();

    page.clickPreview();

    expect(miReportsService.previewCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT account_id FROM accounts' });
    expect(page.previewHeaders).toEqual(['account_id']);
    expect(page.previewCells).toEqual(['UK-E-IN-00001']);
  });

  it('should trim the query before previewing', () => {
    component.form.setValue({ sqlQuery: '  SELECT 1  ' });
    fixture.detectChanges();

    page.clickPreview();

    expect(miReportsService.previewCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT 1' });
  });

  it('should show the error summary and not preview when the query is empty', () => {
    component.form.setValue({ sqlQuery: '' });
    fixture.detectChanges();

    page.clickPreview();

    expect(page.errorSummary).toBeTruthy();
    expect(miReportsService.previewCustomReport).not.toHaveBeenCalled();
  });

  it('should show the error on the query field and not display a preview when the previewed query is invalid', () => {
    const message = 'Invalid SQL query';
    miReportsService.previewCustomReport.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'REPORT1001', message } })),
    );

    component.form.setValue({ sqlQuery: 'SELECT * FROM nonexistent' });
    fixture.detectChanges();

    page.clickPreview();

    expect(page.errorSummary).toBeTruthy();
    expect(component.form.controls.sqlQuery.errors).toEqual({ invalidSqlQuery: message });
    expect(page.previewHeaders).toEqual([]);
  });
});

describe('ViewCustomReportComponent when the report is already a favourite', () => {
  let page: FavouritePage;

  class FavouritePage extends BasePage<ViewCustomReportComponent> {
    get favouriteToggleLink(): HTMLAnchorElement {
      return this.query('a.toggle-favourite');
    }
  }

  beforeEach(async () => {
    const favouriteReport: MiReportUserDefinedDTO = { ...report, favourite: true };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ id: '7' }, null, { report: favouriteReport }) },
        {
          provide: MiReportsUserDefinedService,
          useValue: {
            createFavourite: jest.fn(),
            deleteFavourite: jest.fn(),
            hasManageCustomReportsAccess: jest.fn().mockReturnValue(of(true)),
          },
        },
        { provide: BusinessErrorService, useValue: { showError: jest.fn() } },
      ],
      declarations: [ViewCustomReportComponent],
    }).compileComponents();

    const fixture = TestBed.createComponent(ViewCustomReportComponent);
    page = new FavouritePage(fixture);
    fixture.detectChanges();
  });

  it('should show the report as already favourite when the report DTO has favourite set', () => {
    expect(page.favouriteToggleLink.textContent.trim()).toBe('Remove from your favourites');
  });
});
