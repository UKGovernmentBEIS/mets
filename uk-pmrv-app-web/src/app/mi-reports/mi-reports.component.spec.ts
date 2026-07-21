import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ConfigService } from '@core/config/config.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService, MiReportUserDefinedResults } from 'pmrv-api';

import { miReportTypeDescriptionMap } from './core/mi-report';
import { MiReportsComponent } from './mi-reports.component';

describe('MiReportsComponent', () => {
  let component: MiReportsComponent;
  let fixture: ComponentFixture<MiReportsComponent>;
  let page: Page;
  let miReportsUserDefinedService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let configService: Partial<jest.Mocked<ConfigService>>;

  const standardReports = [
    { id: 1, miReportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS' },
    { id: 2, miReportType: 'COMPLETED_WORK' },
    { id: 3, miReportType: 'REGULATOR_OUTSTANDING_REQUEST_TASKS' },
  ];

  const mockCustomMiReportResult = {
    queries: [
      {
        reportName: 'Test report 1',
        description: 'This is a dummy report 1',
        categories: [
          { id: 2, name: 'Cat 2' },
          { id: 4, name: 'Cat 4' },
          { id: 8, name: 'Cat 8' },
        ],
      },
      {
        reportName: 'Test report 2',
        description: 'This is a dummy report 2',
        categories: [{ id: 3, name: 'Cat 3' }],
      },
    ],
    total: 2,
  } as MiReportUserDefinedResults;

  const mockCategories = [
    { id: 2, name: 'Cat 2' },
    { id: 3, name: 'Cat 3' },
  ];

  class Page extends BasePage<MiReportsComponent> {
    get cells(): HTMLTableCellElement[] {
      return Array.from(this.queryAll<HTMLTableCellElement>('td'));
    }

    get categoryOptions(): HTMLOptionElement[] {
      return Array.from(this.queryAll<HTMLOptionElement>('#filterByCategory option'));
    }

    get standardList(): HTMLLIElement[] {
      return Array.from(this.queryAll<HTMLLIElement>('#standard li'));
    }

    get tabs() {
      return Array.from(this.queryAll<HTMLLIElement>('ul.govuk-tabs__list > li'));
    }

    get notificationBanner(): HTMLElement {
      return this.query('govuk-notification-banner');
    }

    get addCustomReportButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button.add-custom-report');
    }
  }

  const routeStub = new ActivatedRouteStub(null, null, { standardReports });

  beforeEach(async () => {
    miReportsUserDefinedService = {
      getReports: jest.fn().mockReturnValue(of(mockCustomMiReportResult)),
      getCategories: jest.fn().mockReturnValue(of(mockCategories)),
    };

    configService = {
      isFeatureEnabled: jest.fn().mockReturnValue(of(true)),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: MiReportsUserDefinedService, useValue: miReportsUserDefinedService },
        { provide: ConfigService, useValue: configService },
      ],
      declarations: [MiReportsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MiReportsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create list for standard reports with expected content', () => {
    const list = page.standardList;
    expect(list.length).toEqual(3);
    expect(list.map((c) => c.textContent)).not.toContain(miReportTypeDescriptionMap.CUSTOM);
    const reportDescriptions = list.map((c) => c.textContent);
    const expectedDescriptions = standardReports
      .map((r) => miReportTypeDescriptionMap[r.miReportType])
      .sort((a, b) => a.localeCompare(b));

    reportDescriptions.forEach((value, index) => {
      expect(value).toEqual(expectedDescriptions[index]);
    });
  });

  it('should create table for custom reports with expected content', async () => {
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['Test report 1', 'Cat 2,Cat 4,Cat 8', 'This is a dummy report 1'],
      ...['Test report 2', 'Cat 3', 'This is a dummy report 2'],
    ]);
  });

  it('should show the custom reports tab and add button when reportingImprovementsEnabled is on', () => {
    expect(page.addCustomReportButton).toBeTruthy();
    expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Custom reports', 'Standard reports']);
  });

  it('should hide the custom reports tab and add button when reportingImprovementsEnabled is off', () => {
    configService.isFeatureEnabled.mockReturnValue(of(false));

    fixture = TestBed.createComponent(MiReportsComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.addCustomReportButton).toBeFalsy();
    expect(page.cells).toEqual([]);
    expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Standard reports']);
  });

  it('should append the custom sql report to the standard reports list when reportingImprovementsEnabled is off', () => {
    configService.isFeatureEnabled.mockReturnValue(of(false));

    fixture = TestBed.createComponent(MiReportsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    const list = page.standardList;
    expect(list.length).toEqual(4);
    expect(list.map((c) => c.textContent)).toContain(miReportTypeDescriptionMap.CUSTOM);

    let currentData: Array<{ miReportType?: string; link?: string }>;
    component.standardCurrentPageData$.subscribe((data) => (currentData = data));
    expect(currentData.find((data) => data.miReportType === 'CUSTOM').link).toEqual('./custom');
  });

  it('should populate the category filter with an "All" option plus the enabled categories', () => {
    expect(page.categoryOptions.map((option) => option.textContent.trim())).toEqual([
      'All',
      ...mockCategories.map((category) => category.name),
    ]);
  });

  it('should not pass a category id when the "All" option is selected', async () => {
    await fixture.whenStable();

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(0, component.pageSize, undefined);
  });

  it('should reset to the first page and filter by the selected category on change', async () => {
    await fixture.whenStable();
    miReportsUserDefinedService.getReports.mockClear();

    component.customCurrentPage$.next(3);
    component.onCategoryChange(2);
    await fixture.whenStable();

    expect(component.selectedCategory$.value).toEqual(2);
    expect(component.customCurrentPage$.value).toEqual(1);
    // the synchronous page + category updates are coalesced into a single request
    expect(miReportsUserDefinedService.getReports).toHaveBeenCalledTimes(1);
    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(0, component.pageSize, 2);
  });

  it('should show the filtered empty state when a category is selected and no reports match', async () => {
    miReportsUserDefinedService.getReports.mockReturnValue(of({ queries: [], total: 0 } as MiReportUserDefinedResults));
    component.onCategoryChange(2);
    await fixture.whenStable();
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('There are no matching reports.');
    expect(text).not.toContain('There are no reports available.');
  });

  it('should not show the success banner without a notification', () => {
    expect(page.notificationBanner).toBeFalsy();
  });

  it('should show the success banner when navigated to with a notification', () => {
    jest
      .spyOn(TestBed.inject(Router), 'currentNavigation')
      .mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(MiReportsComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.notificationBanner).toBeTruthy();
    expect(page.notificationBanner.textContent).toContain('Report saved');
  });
});
