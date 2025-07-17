import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestDetailsSearchResults, RequestsService } from 'pmrv-api';

import { mockedAccount, mockedAccountPermit, mockWorkflowResults } from '../../../../accounts/testing/mock-data';
import { AviationAccountsStore } from '../../../../aviation/accounts/store';
import {
  mockedAccount as mockedAviationAccount,
  mockWorkflowEMPResults,
} from '../../../../aviation/accounts/testing/mock-data';
import { WorkflowsComponent } from './workflows.component';

describe('WorkflowsComponent', () => {
  let component: WorkflowsComponent;
  let fixture: ComponentFixture<WorkflowsComponent>;
  let page: Page;
  let authStore: AuthStore;
  let store: AviationAccountsStore;

  const requestsService = mockClass(RequestsService);

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    accountPermit: mockedAccountPermit,
  });

  class Page extends BasePage<WorkflowsComponent> {
    get permitApplicationTypeCheckbox() {
      return this.query<HTMLInputElement>('input#workflowTypes-1');
    }

    get cancelledStatusCheckbox() {
      return this.query<HTMLInputElement>('input#workflowStatuses-1');
    }

    get workflowNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li a');
    }

    get workflowStatusNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li .govuk-tag');
    }

    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get checkbox_labels() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__label');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(WorkflowsComponent);
    component = fixture.componentInstance;
    component.currentTab = 'workflows';
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkflowsComponent],
      imports: [SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: RequestsService, useValue: requestsService },
      ],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    authStore = TestBed.inject(AuthStore);
  });

  describe('search filtering by type', () => {
    beforeEach(async () => {
      authStore.setCurrentDomain('INSTALLATION');
      requestsService.getRequestDetailsByResource.mockReturnValue(
        of({ requestDetails: [mockWorkflowResults.requestDetails[0]], total: 1 } as RequestDetailsSearchResults),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking type', () => {
      page.permitApplicationTypeCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByResource).toHaveBeenLastCalledWith({
        resourceType: 'ACCOUNT',
        resourceId: String(mockedAccount.id),
        category: 'PERMIT',
        requestTypes: ['PERMIT_REISSUE'],
        requestStatuses: [],
        pageNumber: 0,
        pageSize: 30,
      });

      expect(page.workflowNames.map((workflowName) => workflowName.textContent.trim())).toEqual([
        '2 Permit application',
      ]);
      expect(page.workflowStatusNames.map((tag) => tag.textContent.trim())).toEqual(['IN PROGRESS']);
    });
  });

  describe('search filtering by status', () => {
    beforeEach(async () => {
      authStore.setCurrentDomain('INSTALLATION');
      requestsService.getRequestDetailsByResource.mockReturnValue(
        of({ requestDetails: [mockWorkflowResults.requestDetails[0]], total: 1 } as RequestDetailsSearchResults),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking status', () => {
      page.cancelledStatusCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByResource).toHaveBeenLastCalledWith({
        resourceType: 'ACCOUNT',
        resourceId: String(mockedAccount.id),
        category: 'PERMIT',
        requestTypes: [],
        requestStatuses: ['CANCELLED'],
        pageNumber: 0,
        pageSize: 30,
      });

      expect(page.workflowNames.map((workflowName) => workflowName.textContent.trim())).toEqual([
        '2 Permit application',
      ]);
      expect(page.workflowStatusNames.map((tag) => tag.textContent.trim())).toEqual(['IN PROGRESS']);
    });
  });

  describe('search filtering by type in aviation domain', () => {
    beforeEach(async () => {
      store.setCurrentAccount(mockedAviationAccount);
      authStore.setCurrentDomain('AVIATION');
      requestsService.getRequestDetailsByResource.mockReturnValue(
        of({ requestDetails: [mockWorkflowEMPResults.requestDetails[0]], total: 1 } as RequestDetailsSearchResults),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking type', () => {
      expect(page.checkboxes.length).toEqual(11);
      expect(page.checkbox_labels[0].innerHTML).toContain('Account closure');
      page.checkboxes[0].click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByResource).toHaveBeenLastCalledWith({
        resourceType: 'ACCOUNT',
        resourceId: String(mockedAviationAccount.aviationAccount.id),
        category: 'PERMIT',
        requestTypes: ['AVIATION_ACCOUNT_CLOSURE'],
        requestStatuses: [],
        pageNumber: 0,
        pageSize: 30,
      });

      expect(page.workflowStatusNames.map((tag) => tag.textContent.trim())).toEqual(['IN PROGRESS']);
    });
  });
});
