import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import {
  CaExternalContactsService,
  OperatorAuthoritiesService,
  TasksAssignmentService,
  TasksService,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

import { EmpNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: EmpNotifyOperatorComponent;
  let fixture: ComponentFixture<EmpNotifyOperatorComponent>;
  let page: Page;
  let operatorAuthoritiesService: MockType<OperatorAuthoritiesService>;
  let externalContactsService: MockType<CaExternalContactsService>;
  let tasksAssignmentService: MockType<TasksAssignmentService>;
  let tasksService: MockType<TasksService>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();
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
  ];

  const mockContacts = {
    caExternalContacts: [
      {
        id: 1,
        name: 'Installation 5 Account',
        email: 'regulator5@trasys.gr',
        description: 'des',
        lastUpdatedDate: '2021-03-03T10:40:03.535662Z',
      },
      {
        id: 2,
        name: 'Legal5',
        email: 'qwe@qwreewrwe',
        description: 'dws',
        lastUpdatedDate: '2021-03-03T10:44:44.71144Z',
      },
    ],
    isEditable: true,
  };

  const mockUsers = {
    authorities: [
      {
        userId: 'cceaad6d-4b09-48bf-9556-77e10f874028',
        firstName: 'newop1',
        lastName: 'User',
        roleName: 'Operator',
        roleCode: 'operator',
        authorityCreationDate: '2021-12-02T12:41:16.752923Z',
        authorityStatus: 'ACTIVE',
        locked: false,
      },
    ] as UserAuthorityInfoDTO[],
    editable: true,
  };

  class Page extends BasePage<EmpNotifyOperatorComponent> {
    get notifyOperator(): HTMLElement {
      return this.query<HTMLElement>('app-notify-operator');
    }
  }

  beforeEach(async () => {
    tasksAssignmentService = {
      getCandidateAssigneesByTaskId: jest.fn().mockReturnValue(of(mockAssignees)),
    };

    externalContactsService = {
      getCaExternalContacts: jest.fn().mockReturnValue(asyncData(mockContacts)),
    };
    operatorAuthoritiesService = {
      getAccountOperatorAuthorities: jest.fn().mockReturnValue(asyncData(mockUsers)),
    };

    tasksService = {
      processRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [EmpNotifyOperatorComponent, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: CaExternalContactsService, useValue: externalContactsService },
        { provide: TasksService, useValue: tasksService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        DestroySubject,
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();
    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: {
          id: 'EMP0001',
          accountId: 1,
        },
        requestTask: {
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          payload: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            determination: { type: 'APPROVED', reason: 'Reason' },
          } as EmpRequestTaskPayloadUkEts | EmpRequestTaskPayloadCorsia,
        },
        allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION'],
      },
    });

    fixture = TestBed.createComponent(EmpNotifyOperatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.notifyOperator).toBeTruthy();
  });
});
