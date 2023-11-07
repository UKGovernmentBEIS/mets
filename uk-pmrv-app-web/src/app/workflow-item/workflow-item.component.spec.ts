import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestActionsService, RequestItemsService, RequestsService } from 'pmrv-api';

import { AuthStore } from '../core/store/auth';
import { ItemLinkPipe } from '../shared/pipes/item-link.pipe';
import { UrlRequestType } from '../shared/types/url-request-type';
import { RequestNotesComponent } from './notes/request-notes.component';
import { WorkflowRelatedCreateActionsComponent } from './shared/workflow-related-create-actions/workflow-related-create-actions.component';
import { WorkflowItemComponent } from './workflow-item.component';

describe('WorkflowItemComponent', () => {
  let component: WorkflowItemComponent;
  let fixture: ComponentFixture<WorkflowItemComponent>;
  let page: Page;
  let authStore: AuthStore;

  const requestsService = mockClass(RequestsService);
  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);

  class Page extends BasePage<WorkflowItemComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
    }
    get tasks() {
      return this.queryAll<HTMLElement>('app-related-tasks h3');
    }
    get timeline() {
      return this.queryAll<HTMLElement>('app-timeline-item h3');
    }
    get relatedCreateActions() {
      return this.queryAll<HTMLLIElement>('app-workflow-related-create-actions ul > li');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(WorkflowItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  afterEach(async () => {
    jest.clearAllMocks();
  });

  describe('for account workflow item', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [WorkflowItemComponent, WorkflowRelatedCreateActionsComponent, RequestNotesComponent],
        providers: [
          { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ accountId: 1, 'request-id': '1' }) },
          { provide: RequestsService, useValue: requestsService },
          { provide: RequestItemsService, useValue: requestItemsService },
          { provide: RequestActionsService, useValue: requestActionsService },
          ItemLinkPipe,
        ],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    });

    describe('display all info', () => {
      beforeEach(() => {
        authStore = TestBed.inject(AuthStore);
        authStore.setUserState({
          ...authStore.getState().userState,
          domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
          roleType: 'REGULATOR',
          userId: 'opTestId',
        });

        requestsService.getRequestDetailsById.mockReturnValue(
          of({
            id: '1',
            requestType: 'PERMIT_ISSUANCE',
            requestStatus: 'IN_PROGRESS',
            creationDate: '22-2-2022',
          }),
        );

        requestItemsService.getItemsByRequest.mockReturnValue(
          of({
            items: [
              {
                taskId: 1,
                requestType: 'INSTALLATION_ACCOUNT_OPENING',
                taskType: 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE',
              },
              {
                taskId: 2,
                requestType: 'INSTALLATION_ACCOUNT_OPENING',
                taskType: 'ACCOUNT_USERS_SETUP',
              },
            ],
          }),
        );

        requestActionsService.getRequestActionsByRequestId.mockReturnValue(
          of([
            {
              id: 1,
              creationDate: '2020-08-25 10:36:15.189643',
              submitter: 'Operator',
              type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
            },
            {
              id: 2,
              creationDate: '2020-08-26 10:36:15.189643',
              submitter: 'Regulator',
              type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
            },
          ]),
        );
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display request details', () => {
        expect(page.heading.textContent.trim()).toEqual('Permit application IN PROGRESS');
      });

      it('should display tasks to complete', () => {
        expect(page.tasks).toBeTruthy();
        expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
          'Archive installation account application',
          'Manage account users',
        ]);
      });

      it('should display timeline', () => {
        expect(page.timeline).toBeTruthy();
        expect(page.timeline.map((el) => el.textContent.trim())).toEqual([
          'The regulator accepted the installation account application',
          'Original application',
        ]);
      });

      it('should not invoke aer workflows', () => {
        expect(requestsService.getAvailableAerWorkflows).not.toHaveBeenCalled();
      });
    });

    describe('display only request details', () => {
      beforeEach(() => {
        requestsService.getRequestDetailsById.mockReturnValue(
          of({
            id: '1',
            requestType: 'PERMIT_ISSUANCE',
            requestStatus: 'IN_PROGRESS',
            creationDate: '22-2-2022',
          }),
        );

        requestItemsService.getItemsByRequest.mockReturnValue(of({}));

        requestActionsService.getRequestActionsByRequestId.mockReturnValue(of([]));
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display request details', () => {
        expect(page.heading.textContent.trim()).toEqual('Permit application IN PROGRESS');
      });

      it('should not display any task to complete', () => {
        expect(page.tasks).toBeTruthy();
        expect(page.tasks.map((el) => el.textContent)).toEqual([]);
      });

      it('should not display timeline', () => {
        expect(page.timeline).toBeTruthy();
        expect(page.timeline.map((el) => el.textContent)).toEqual([]);
      });
    });

    describe('display related create actions for AER workflow when regulator', () => {
      beforeEach(() => {
        authStore = TestBed.inject(AuthStore);
        authStore.setUserState({
          ...authStore.getState().userState,
          domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
          roleType: 'REGULATOR',
          userId: 'opTestId',
        });

        requestsService.getRequestDetailsById.mockReturnValue(
          of({
            id: '1',
            requestType: 'AER',
            requestStatus: 'COMPLETED',
            creationDate: '22-2-2022',
          }),
        );

        requestItemsService.getItemsByRequest.mockReturnValue(
          of({
            items: [],
          }),
        );

        requestActionsService.getRequestActionsByRequestId.mockReturnValue(of([]));
        requestsService.getAvailableAerWorkflows.mockReturnValue(
          of({
            DRE: { valid: true },
          }),
        );
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should invoke aer workflows', () => {
        expect(requestsService.getRequestDetailsById).toHaveBeenCalled();
        expect(requestsService.getAvailableAerWorkflows).toHaveBeenCalled();
        expect(page.relatedCreateActions.map((li) => li.textContent.trim())).toEqual([
          'Determine reportable emissions',
        ]);
      });
    });

    describe('do not display related create actions for AER workflow when not regulator', () => {
      beforeEach(() => {
        authStore = TestBed.inject(AuthStore);
        authStore.setUserState({
          ...authStore.getState().userState,
          domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
          roleType: 'OPERATOR',
          userId: 'opTestId',
        });

        requestsService.getRequestDetailsById.mockReturnValue(
          of({
            id: '1',
            requestType: 'AER',
            requestStatus: 'COMPLETED',
            creationDate: '22-2-2022',
          }),
        );

        requestItemsService.getItemsByRequest.mockReturnValue(
          of({
            items: [],
          }),
        );

        requestActionsService.getRequestActionsByRequestId.mockReturnValue(of([]));
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should invoke aer workflows', () => {
        expect(requestsService.getRequestDetailsById).toHaveBeenCalled();
        expect(requestsService.getAvailableAerWorkflows).not.toHaveBeenCalled();
      });
    });
  });

  describe('for CA workflow item', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [WorkflowItemComponent, WorkflowRelatedCreateActionsComponent, RequestNotesComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: new ActivatedRouteStub({ 'request-id': '1' }, undefined, {
              requestType: 'batch-variation' as UrlRequestType,
            }),
          },
          { provide: RequestsService, useValue: requestsService },
          { provide: RequestItemsService, useValue: requestItemsService },
          { provide: RequestActionsService, useValue: requestActionsService },
          ItemLinkPipe,
        ],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    });

    describe('display CA request details with timeline events', () => {
      beforeEach(() => {
        authStore = TestBed.inject(AuthStore);
        authStore.setUserState({
          ...authStore.getState().userState,
          domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
          roleType: 'REGULATOR',
          userId: 'opTestId',
        });

        requestsService.getRequestDetailsById.mockReturnValue(
          of({
            id: '1',
            requestType: 'PERMIT_BATCH_REISSUE',
            requestStatus: 'COMPLETED',
            creationDate: '22-2-2022',
          }),
        );

        requestItemsService.getItemsByRequest.mockReturnValue(
          of({
            items: [],
          }),
        );

        requestActionsService.getRequestActionsByRequestId.mockReturnValue(
          of([
            {
              id: 1,
              creationDate: '2022-02-02 10:36:15.189643',
              submitter: 'regSubmitter',
              type: 'BATCH_REISSUE_SUBMITTED',
            },
            {
              id: 2,
              creationDate: '2022-02-03 10:36:15.189643',
              submitter: 'regSubmitter',
              type: 'BATCH_REISSUE_COMPLETED',
            },
          ]),
        );
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display request details', () => {
        expect(page.heading.textContent.trim()).toEqual('Batch variation COMPLETED');
      });

      it('should display timeline', () => {
        expect(page.timeline).toBeTruthy();
        expect(page.timeline.map((el) => el.textContent.trim())).toEqual([
          'Batch variation completed by regSubmitter',
          'Batch variation submitted by regSubmitter',
        ]);
      });

      it('should not invoke aer workflows', () => {
        expect(requestsService.getAvailableAerWorkflows).not.toHaveBeenCalled();
      });
    });
  });
});
