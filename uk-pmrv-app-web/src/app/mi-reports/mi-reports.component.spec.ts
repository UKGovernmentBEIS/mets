import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ConfigService } from '@core/config/config.service';
import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService, MiReportUserDefinedResults } from 'pmrv-api';

import { miReportTypeDescriptionMap } from './core/mi-report';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsStore } from './store';
import { mockMiReportCategories, mockMiReportUserDefinedResults, mockStandardReports } from './testing/mock-data';

describe('MiReportsComponent', () => {
  let component: MiReportsComponent;
  let fixture: ComponentFixture<MiReportsComponent>;
  let store: MiReportsStore;
  let page: Page;
  let miReportsUserDefinedService: Partial<jest.Mocked<MiReportsUserDefinedService>>;
  let configService: Partial<jest.Mocked<ConfigService>>;

  class Page extends BasePage<MiReportsComponent> {
    get cells(): HTMLTableCellElement[] {
      return Array.from(this.queryAll<HTMLTableCellElement>('td'));
    }

    get categoryOptions(): HTMLOptionElement[] {
      return Array.from(this.queryAll<HTMLOptionElement>('#filterByCategory option'));
    }

    get searchInput(): HTMLInputElement {
      return this.query<HTMLInputElement>('#searchTerm');
    }

    get searchLabel(): HTMLLabelElement {
      return this.query<HTMLLabelElement>('label[for="searchTerm"]');
    }

    get showFavouritesOnlyCheckbox(): HTMLInputElement {
      return this.query<HTMLInputElement>('#showFavouritesOnly');
    }

    get showFavouritesOnlyLabel(): HTMLLabelElement {
      return this.query<HTMLLabelElement>('label[for="showFavouritesOnly"]');
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

  const routeStub = new ActivatedRouteStub(null, null, { standardReports: mockStandardReports });

  beforeEach(async () => {
    miReportsUserDefinedService = {
      getReports: jest.fn().mockReturnValue(of(mockMiReportUserDefinedResults)),
      getCategories: jest.fn().mockReturnValue(of(mockMiReportCategories)),
      hasManageCustomReportsAccess: jest.fn().mockReturnValue(of(true)),
    };

    configService = {
      isFeatureEnabled: jest.fn().mockReturnValue(of(true)),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        MiReportsStore,
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: MiReportsUserDefinedService, useValue: miReportsUserDefinedService },
        { provide: ConfigService, useValue: configService },
      ],
      declarations: [MiReportsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    TestBed.inject(AuthStore).setCurrentDomain('INSTALLATION');
    store = TestBed.inject(MiReportsStore);
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
    const expectedDescriptions = mockStandardReports
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
      ...['Test report 1', 'Cat 2, Cat 4, Cat 8', 'This is a dummy report 1'],
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

  it('should hide the add button, but still show the custom reports tab, when the user lacks execute permission for custom reports', () => {
    miReportsUserDefinedService.hasManageCustomReportsAccess.mockReturnValue(of(false));

    fixture = TestBed.createComponent(MiReportsComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.addCustomReportButton).toBeFalsy();
    expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Custom reports', 'Standard reports']);
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
      ...mockMiReportCategories.map((category) => category.name),
    ]);
  });

  it('should not pass a category id when the "All" option is selected', async () => {
    await fixture.whenStable();

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      undefined,
      undefined,
    );
  });

  it('should reset to the first page and filter by the selected category on change', fakeAsync(() => {
    miReportsUserDefinedService.getReports.mockClear();

    store.setPage(3);
    component.onCategoryChange(2);
    tick();

    expect(store.getState().selectedCategory).toEqual(2);
    expect(store.getState().page).toEqual(1);
    // the page change and the category change are coalesced into a single request
    expect(miReportsUserDefinedService.getReports).toHaveBeenCalledTimes(1);
    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      2,
      undefined,
      undefined,
    );
  }));

  it('should display the search field with its label', () => {
    expect(page.searchInput).toBeTruthy();
    expect(page.searchLabel.textContent.trim()).toEqual('Search by report name or description');
  });

  it('should reset to the first page and search with the debounced term', fakeAsync(() => {
    store.setPage(3);
    component.onSearchTermChange('dummy');

    expect(store.getState().page).toEqual(1);
    // the search is debounced, so the service is not called with the term immediately
    expect(miReportsUserDefinedService.getReports).not.toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      'dummy',
      undefined,
    );

    tick(300);

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      'dummy',
      undefined,
    );
  }));

  it('should not pass a term shorter than 3 characters', fakeAsync(() => {
    component.onSearchTermChange('du');
    tick(300);

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      undefined,
      undefined,
    );
  }));

  it('should search without a term when the search field is cleared', fakeAsync(() => {
    component.onSearchTermChange('dummy');
    tick(300);
    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      'dummy',
      undefined,
    );

    component.onSearchTermChange('');
    tick(300);

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      undefined,
      undefined,
    );
  }));

  it('should restore the search term and category from the store when revisiting the page', fakeAsync(() => {
    component.onSearchTermChange('dummy');
    component.onCategoryChange(2);
    tick(300);

    fixture.destroy();
    miReportsUserDefinedService.getReports.mockClear();

    fixture = TestBed.createComponent(MiReportsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    // the restored term is applied immediately, without waiting for the debounce delay
    expect(page.searchInput.value).toEqual('dummy');
    expect(miReportsUserDefinedService.getReports).toHaveBeenCalledTimes(1);
    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      2,
      'dummy',
      undefined,
    );
  }));

  it('should show the filtered empty state when a search term matches no reports', fakeAsync(() => {
    miReportsUserDefinedService.getReports.mockReturnValue(of({ queries: [], total: 0 } as MiReportUserDefinedResults));
    component.onSearchTermChange('no match');
    tick(300);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('There are no matching reports.');
    expect(text).not.toContain('There are no reports available.');
  }));

  it('should show the filtered empty state when a category is selected and no reports match', async () => {
    miReportsUserDefinedService.getReports.mockReturnValue(of({ queries: [], total: 0 } as MiReportUserDefinedResults));
    component.onCategoryChange(2);
    await fixture.whenStable();
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('There are no matching reports.');
    expect(text).not.toContain('There are no reports available.');
  });

  it('should display the show only my favourites checkbox with its label', () => {
    expect(page.showFavouritesOnlyCheckbox).toBeTruthy();
    expect(page.showFavouritesOnlyLabel.textContent.trim()).toEqual('Show only my favourites');
  });

  it('should pass the favourites filter to getReports and render whatever it returns when the checkbox is enabled', fakeAsync(() => {
    miReportsUserDefinedService.getReports.mockReturnValue(
      of({ queries: [mockMiReportUserDefinedResults.queries[0]], total: 1 } as MiReportUserDefinedResults),
    );

    component.onShowFavouritesOnlyChange(true);
    tick();
    fixture.detectChanges();

    expect(store.getState().page).toEqual(1);
    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      undefined,
      true,
    );
    expect(page.cells.map((cell) => cell.textContent.trim())).toEqual([
      'Test report 1',
      'Cat 2, Cat 4, Cat 8',
      'This is a dummy report 1',
    ]);
  }));

  it('should combine the favourites filter with the category filter', fakeAsync(() => {
    component.onCategoryChange(3);
    component.onShowFavouritesOnlyChange(true);
    tick();
    fixture.detectChanges();

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      3,
      undefined,
      true,
    );
  }));

  it('should restore all the reports when the favourites filter is disabled again', fakeAsync(() => {
    miReportsUserDefinedService.getReports.mockReturnValue(of({ queries: [], total: 0 } as MiReportUserDefinedResults));

    component.onShowFavouritesOnlyChange(true);
    tick();
    fixture.detectChanges();
    expect(page.cells).toEqual([]);

    miReportsUserDefinedService.getReports.mockReturnValue(of(mockMiReportUserDefinedResults));
    component.onShowFavouritesOnlyChange(false);
    tick();
    fixture.detectChanges();

    expect(miReportsUserDefinedService.getReports).toHaveBeenLastCalledWith(
      'INSTALLATION',
      0,
      component.pageSize,
      undefined,
      undefined,
      undefined,
    );
    expect(page.cells.length).toEqual(6);
  }));

  it('should show the filtered empty state when the favourites filter matches no reports', fakeAsync(() => {
    miReportsUserDefinedService.getReports.mockReturnValue(of({ queries: [], total: 0 } as MiReportUserDefinedResults));

    component.onShowFavouritesOnlyChange(true);
    tick();
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('There are no matching reports.');
    expect(text).not.toContain('There are no reports available.');
  }));

  it('should not show the success banner without a notification', () => {
    expect(page.notificationBanner).toBeFalsy();
  });

  it('should show the success banner when navigated to with a notification', () => {
    jest
      .spyOn(TestBed.inject(Router), 'currentNavigation')
      .mockReturnValue({ extras: { state: { notification: 'Report saved' } } } as any);

    fixture = TestBed.createComponent(MiReportsComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.notificationBanner).toBeTruthy();
    expect(page.notificationBanner.textContent).toContain('Report saved');
  });

  it('should show the deletion success banner when navigated to after deleting a report', () => {
    jest
      .spyOn(TestBed.inject(Router), 'currentNavigation')
      .mockReturnValue({ extras: { state: { notification: 'The report has been deleted' } } } as any);

    fixture = TestBed.createComponent(MiReportsComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.notificationBanner).toBeTruthy();
    expect(page.notificationBanner.textContent).toContain('The report has been deleted');
  });
});
