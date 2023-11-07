import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, MockType } from '@testing';

import {
  AviationAccountViewService,
  CaExternalContactsService,
  OperatorAuthoritiesService,
  TasksAssignmentService,
  TasksService,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

import { DreNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: DreNotifyOperatorComponent;
  let fixture: ComponentFixture<DreNotifyOperatorComponent>;
  let operatorAuthoritiesService: MockType<OperatorAuthoritiesService>;
  let externalContactsService: MockType<CaExternalContactsService>;
  let tasksAssignmentService: MockType<TasksAssignmentService>;
  let tasksService: MockType<TasksService>;

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

  const mockRoute = {
    paramMap: of({ taskId: '123' }),
  };

  const mockRequestTaskStore = {
    selectSnapshot: jest.fn(() => ({ requestTaskItem: { requestInfo: { accountId: 456 } } })),
    pipe: jest.fn(() => of(['SOME_OTHER_ACTION'])),
  };

  const mockAccountViewService = {
    getAviationAccountById: jest.fn().mockReturnValue(of({ aviationAccount: { location: 'some_location' } })),
  };

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
      imports: [DreNotifyOperatorComponent, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: mockRoute },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: CaExternalContactsService, useValue: externalContactsService },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskStore, useValue: mockRequestTaskStore },
        { provide: AviationAccountViewService, useValue: mockAccountViewService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DreNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update vm$ correctly', () => {
    const mockAccount = { aviationAccount: { location: 'some_location' } };
    const accountId = 456;
    mockAccountViewService.getAviationAccountById.mockReturnValue(of(mockAccount));

    route.setParamMap({ taskId: '123' });

    fixture.detectChanges();

    expect(component.vm$).toBeTruthy();
    component.vm$.subscribe((viewModel) => {
      expect(viewModel).toEqual({
        taskId: 123,
        accountId: accountId,
        hasLocation: true,
      });
    });
  });

  it('should initialize isForSubmission$ correctly', () => {
    component.isForSubmission$.subscribe((value) => {
      expect(value).toBe(true);
    });
  });
});
