import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { ReportsComponent } from '@shared/accounts';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { AccountReportingService, RequestsService } from 'pmrv-api';

import { AccountsModule } from '../../../../accounts/accounts.module';
import {
  mockedAccount,
  mockedAccountPermit,
  mockReportsResults,
  mockYearEmissionsResults,
} from '../../../../accounts/testing/mock-data';
import { SharedUserModule } from '../../../../shared-user/shared-user.module';

describe('ReportsComponent', () => {
  let component: ReportsComponent;
  let fixture: ComponentFixture<ReportsComponent>;
  let page: Page;
  let authStore: AuthStore;

  const requestsService = mockClass(RequestsService);
  const accountReportingService = mockClass(AccountReportingService);

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    accountPermit: mockedAccountPermit,
  });

  class Page extends BasePage<ReportsComponent> {
    get emissionReportsTypeCheckbox() {
      return this.query<HTMLInputElement>('input#reportsTypes-3');
    }

    get completedStatusCheckbox() {
      return this.query<HTMLInputElement>('input#reportsStatuses-3');
    }

    get reportsHeader() {
      return this.queryAll<HTMLLIElement>('.search-results-list_item .govuk-heading-m');
    }

    get reportsNames() {
      return this.queryAll<HTMLLIElement>('.govuk-list > li a');
    }

    get reportsStatusNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li .govuk-tag');
    }
    get reportDates() {
      return this.queryAll<HTMLLIElement>('.govuk-list > li .govuk-body-s');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(ReportsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReportsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule, AccountsModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: RequestsService, useValue: requestsService },
        { provide: AccountReportingService, useValue: accountReportingService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
  });

  describe('search filtering by type', () => {
    beforeEach(async () => {
      const aerReports = mockReportsResults.requestDetails.filter((report) => report.requestType === 'AER');

      requestsService.getRequestDetailsByAccountId.mockReturnValue(
        of({
          requestDetails: [...aerReports],
          total: aerReports.length,
        }),
      );

      accountReportingService.getReportableEmissions.mockReturnValue(of(mockYearEmissionsResults));
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking type', () => {
      page.emissionReportsTypeCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByAccountId).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByAccountId).toHaveBeenLastCalledWith({
        accountId: mockedAccount.id,
        category: 'REPORTING',
        requestTypes: ['AER'],
        requestStatuses: [],
        pageNumber: 0,
        pageSize: 999999,
      });

      expect(page.reportsHeader.map((reportsHeader) => reportsHeader.textContent.trim())).toEqual(['2021', '2020']);

      expect(page.reportsNames.map((reportsName) => reportsName.textContent.trim())).toEqual([
        '2021  emissions report',
        '2020  emissions report',
        '2020  emissions report',
      ]);
      expect(page.reportsStatusNames.map((tag) => tag.textContent.trim())).toEqual([
        'COMPLETED',
        'IN PROGRESS',
        'COMPLETED',
      ]);
    });
  });

  describe('search filtering by status', () => {
    beforeEach(async () => {
      const closedReports = mockReportsResults.requestDetails.filter((report) => report.requestStatus === 'COMPLETED');

      requestsService.getRequestDetailsByAccountId.mockReturnValue(
        of({ requestDetails: [...closedReports], total: closedReports.length }),
      );

      accountReportingService.getReportableEmissions.mockReturnValue(of(mockYearEmissionsResults));
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking status', () => {
      page.completedStatusCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByAccountId).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByAccountId).toHaveBeenLastCalledWith({
        accountId: mockedAccount.id,
        category: 'REPORTING',
        requestTypes: [],
        requestStatuses: ['COMPLETED'],
        pageNumber: 0,
        pageSize: 999999,
      });

      expect(page.reportsNames.map((reportsName) => reportsName.textContent.trim())).toEqual([
        '2021  emissions report',
        '2020  emissions report',
      ]);

      expect(page.reportsStatusNames.map((tag) => tag.textContent.trim())).toEqual(['COMPLETED', 'COMPLETED']);
    });
  });

  describe('reports created date', () => {
    beforeEach(async () => {
      const reports = mockReportsResults.requestDetails;
      requestsService.getRequestDetailsByAccountId.mockReturnValue(
        of({
          requestDetails: [...reports],
          total: reports.length,
        }),
      );
      accountReportingService.getReportableEmissions.mockReturnValue(of(mockYearEmissionsResults));
    });
    beforeEach(createComponent);
    it('should display the date the report was created', () => {
      expect(page.reportDates.length).toEqual(page.reportsNames.length);
    });
  });
});
