import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { AccountsModule } from 'src/app/accounts/accounts.module';
import { mockedAccount, mockedAccountPermit, mockInspectionsResults } from 'src/app/accounts/testing/mock-data';
import { SharedUserModule } from 'src/app/shared-user/shared-user.module';

import { InspectionsComponent } from './inspections.component';

describe('InspectionsComponent', () => {
  let component: InspectionsComponent;
  let fixture: ComponentFixture<InspectionsComponent>;
  let page: Page;
  let authStore: AuthStore;

  const requestsService = mockClass(RequestsService);

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    accountPermit: mockedAccountPermit,
  });

  class Page extends BasePage<InspectionsComponent> {
    get onSiteInspectionsTypeCheck() {
      return this.query<HTMLInputElement>('input#types-0');
    }

    get completedStatusCheckbox() {
      return this.query<HTMLInputElement>('input#statuses-1');
    }

    get yearsGroup() {
      return this.queryAll<HTMLLIElement>('.search-results-list_item .govuk-heading-m');
    }

    get inspectionsNames() {
      return this.queryAll<HTMLLIElement>('.govuk-list > li a');
    }

    get inspectionsStatusNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li .govuk-tag');
    }
    get reportDates() {
      return this.queryAll<HTMLLIElement>('.govuk-list > li .govuk-body-s');
    }
  }

  const createComponent = async () => {
    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');

    fixture = TestBed.createComponent(InspectionsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedUserModule, AccountsModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: RequestsService, useValue: requestsService },
      ],
    }).compileComponents();
  });

  describe('search filtering by type', () => {
    beforeEach(async () => {
      const mockInspections = mockInspectionsResults.requestDetails;

      requestsService.getRequestDetailsByResource.mockReturnValue(
        of({
          requestDetails: [...mockInspections],
          total: mockInspections.length,
        }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking type', () => {
      expect(page.yearsGroup.map((reportsHeader) => reportsHeader.textContent.trim())).toEqual([
        '2024',
        '2022',
        '2021',
      ]);

      expect(page.inspectionsNames.map((reportsName) => reportsName.textContent.trim())).toEqual([
        'INS00055-4  On-site inspection',
        'INS00055-3  On-site inspection',
        'INS00055-2022-1  2022  Audit report',
        'INS00055-2021-1  2021  Audit report',
      ]);
      expect(page.inspectionsStatusNames.map((tag) => tag.textContent.trim())).toEqual([
        'IN PROGRESS',
        'COMPLETED',
        'COMPLETED',
        'IN PROGRESS',
      ]);

      page.onSiteInspectionsTypeCheck.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByResource).toHaveBeenLastCalledWith({
        resourceType: 'ACCOUNT',
        resourceId: String(mockedAccount.id),
        category: 'INSPECTION',
        requestTypes: ['INSTALLATION_ONSITE_INSPECTION'],
        requestStatuses: [],
        pageNumber: 0,
        pageSize: 999999,
      });
    });
  });

  describe('search filtering by status', () => {
    beforeEach(async () => {
      const completedInspections = mockInspectionsResults.requestDetails.filter(
        (report) => report.requestStatus === 'COMPLETED',
      );

      requestsService.getRequestDetailsByResource.mockReturnValue(
        of({ requestDetails: [...completedInspections], total: completedInspections.length }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking status', () => {
      page.completedStatusCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByResource).toHaveBeenLastCalledWith({
        resourceType: 'ACCOUNT',
        resourceId: String(mockedAccount.id),
        category: 'INSPECTION',
        requestTypes: [],
        requestStatuses: ['COMPLETED'],
        pageNumber: 0,
        pageSize: 999999,
      });

      expect(page.inspectionsNames.map((reportsName) => reportsName.textContent.trim())).toEqual([
        'INS00055-3  On-site inspection',
        'INS00055-2022-1  2022  Audit report',
      ]);

      expect(page.inspectionsStatusNames.map((tag) => tag.textContent.trim())).toEqual(['COMPLETED', 'COMPLETED']);
    });
  });
});
