import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { AuthStore } from '@core/store/auth';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksAssignmentService, TasksService } from 'pmrv-api';

import { mockReviewState as permitissuanceReviewState } from '../../../permit-application/testing/mock-state';
import { mockTaskState as revocationMockState } from '../../../permit-revocation/testing/mock-state';
import { PermitSurrenderStore } from '../../../permit-surrender/store/permit-surrender.store';
import { mockTaskState as surrenderMockState } from '../../../permit-surrender/testing/mock-state';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import { mockPermitVariationRegulatorLedPayload } from '../../../permit-variation/testing/mock';
import { SharedModule } from '../../shared.module';
import { StoreContextResolver } from '../../store-resolver/store-context.resolver';
import { PeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let page: Page;
  let component: PeerReviewComponent;
  let fixture: ComponentFixture<PeerReviewComponent>;
  let authStore: AuthStore;

  const tasksService = mockClass(TasksService);
  const tasksAssignmentService = mockClass(TasksAssignmentService);
  const storeResolver = mockClass(StoreContextResolver);

  let location: Location;

  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '237' });

  const mockAssignees = [
    {
      id: '45b2620b-c859-4296-bb58-e49f180f6137',
      firstName: 'Regulator5',
      lastName: 'User',
    },
    {
      id: 'eaa82cc8-0a7d-4f2d-bcf7-f54f612f59e5',
      firstName: 'newreg1',
      lastName: 'User',
    },
    {
      id: '44c7a770-18b2-40e8-85ee-5c92210618d7',
      firstName: 'newreg2',
      lastName: 'User',
    },
  ];

  class Page extends BasePage<PeerReviewComponent> {
    set assignees(value: string) {
      this.setInputValue('#assignees', value);
    }

    get assigneesList() {
      return Array.from(this.query<HTMLDivElement>('#assignees').querySelectorAll('option')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    tasksService.processRequestTaskAction.mockReturnValue(of({}));
    tasksAssignmentService.getCandidateAssigneesByTaskType.mockReturnValue(of(mockAssignees));

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserProfile({
      id: 'eaa82cc8-0a7d-4f2d-bcf7-f54f612f59e5',
      firstName: 'newreg1',
      lastName: 'User',
    });
    location = TestBed.inject(Location);

    jest.clearAllMocks();
  });

  describe('for permit issuance', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_ISSUANCE_APPLICATION_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const permitStore = TestBed.inject(PermitIssuanceStore);

      permitStore.setState({
        ...permitissuanceReviewState,
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_REQUEST_PEER_REVIEW'],
        requestId: '1',
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for permit surrender', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/permit-surrender/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_SURRENDER_APPLICATION_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const surrenderStore = TestBed.inject(PermitSurrenderStore);
      surrenderStore.setState({
        ...surrenderMockState,
        requestTaskType: 'PERMIT_SURRENDER_APPLICATION_REVIEW',
        allowedRequestTaskActions: ['PERMIT_SURRENDER_REQUEST_PEER_REVIEW'],
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for permit revocation', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/permit-revocation/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_REVOCATION_APPLICATION_SUBMIT'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const revocationStore = TestBed.inject(PermitRevocationStore);
      revocationStore.setState({
        ...revocationMockState,
        requestTaskType: 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_REVOCATION_REQUEST_PEER_REVIEW'],
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for permit variation', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/permit-variation/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const permitStore = TestBed.inject(PermitVariationStore);
      permitStore.setState({
        ...mockPermitVariationRegulatorLedPayload,
        allowedRequestTaskActions: ['PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED'],
        requestId: '1',
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for emmisions monitoring plan application', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/237/emp/review/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('EMP_ISSUANCE_UKETS_APPLICATION_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const aviationStore = TestBed.inject(RequestTaskStore);

      const state = aviationStore.getState();

      aviationStore.setState({
        ...state,
        requestTaskItem: {
          ...state.requestTaskItem,
          allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW'],
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
            payload: {
              ...EmpUkEtsStoreDelegate.INITIAL_STATE,
              reviewSectionsCompleted: {},
            } as EmpRequestTaskPayloadUkEts,
          },
        },
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for emmisions monitoring plan variation', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/237/emp/review/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('EMP_VARIATION_UKETS_APPLICATION_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const aviationStore = TestBed.inject(RequestTaskStore);

      const state = aviationStore.getState();

      aviationStore.setState({
        ...state,
        requestTaskItem: {
          ...state.requestTaskItem,
          allowedRequestTaskActions: ['EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW'],
          requestTask: {
            type: 'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
            payload: {
              ...EmpUkEtsStoreDelegate.INITIAL_STATE,
              reviewSectionsCompleted: {},
            } as EmpRequestTaskPayloadUkEts,
          },
        },
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for emmisions monitoring plan corsia variation', () => {
    beforeEach(() => {
      jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/237/emp-corsia/variation/review/peer-review');
      storeResolver.getRequestTaskType.mockReturnValue(of('EMP_VARIATION_CORSIA_APPLICATION_REVIEW'));
      storeResolver.getRequestId.mockReturnValue(of('1'));

      const aviationStore = TestBed.inject(RequestTaskStore);

      const state = aviationStore.getState();

      aviationStore.setState({
        ...state,
        requestTaskItem: {
          ...state.requestTaskItem,
          allowedRequestTaskActions: ['EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW'],
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
            payload: {
              ...EmpCorsiaStoreDelegate.INITIAL_STATE,
              reviewSectionsCompleted: {},
            } as EmpRequestTaskPayloadCorsia,
          },
        },
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskType).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });
});
