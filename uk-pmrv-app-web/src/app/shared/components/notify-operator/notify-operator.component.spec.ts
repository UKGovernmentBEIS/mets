import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, MockType } from '@testing';

import {
  CaExternalContactsService,
  OperatorAuthoritiesService,
  TasksAssignmentService,
  TasksService,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

import { NotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: NotifyOperatorComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostElement: HTMLElement;

  let route: ActivatedRouteStub;
  let operatorAuthoritiesService: MockType<OperatorAuthoritiesService>;
  let externalContactsService: MockType<CaExternalContactsService>;
  let tasksAssignmentService: MockType<TasksAssignmentService>;
  let tasksService: MockType<TasksService>;

  @Component({
    template: `
      <app-notify-operator
        [taskId]="237"
        [accountId]="1"
        requestTaskActionType="PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION"
        [pendingRfi]="false"
        [pendingRde]="true"
        [confirmationMessage]="'Surrender completed'"
      ></app-notify-operator>
    `,
  })
  class TestComponent {}

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
    {
      id: 'cceaad6d-4b09-48bf-9556-77e10f874028',
      firstName: 'newop1',
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
      {
        userId: 'a9f0621d-3097-46f5-b26b-7aeceb8ab146',
        firstName: 'emcon1',
        lastName: 'User',
        roleName: 'Emitter Contact',
        roleCode: 'emitter_contact',
        authorityCreationDate: '2021-12-02T12:52:09.505285Z',
        authorityStatus: 'ACTIVE',
        locked: true,
      },
    ] as UserAuthorityInfoDTO[],
    editable: true,
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

    route = new ActivatedRouteStub({ taskId: '237', index: '0' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TestComponent],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: ActivatedRoute, useValue: route },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: CaExternalContactsService, useValue: externalContactsService },
        { provide: TasksService, useValue: tasksService },
        DestroySubject,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(NotifyOperatorComponent)).componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form ', () => {
    let errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).toBeFalsy();

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.nativeElement.click();
    fixture.detectChanges();

    errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).toBeTruthy();

    component.form.get('assignees').setValue(mockAssignees[0].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);
    submitButton.nativeElement.click();
    fixture.detectChanges();

    errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).toBeFalsy();
    expect(hostElement.innerHTML).toContain('Surrender completed');

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
        decisionNotification: {
          operators: ['cceaad6d-4b09-48bf-9556-77e10f874028'],
          externalContacts: [],
          signatory: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      },
    });
  });

  it('should display error message when template NOTIF1000 error returns from api', () => {
    component.form.get('assignees').setValue(mockAssignees[0].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);

    jest.spyOn(tasksService, 'processRequestTaskAction').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'NOTIF1000',
              data: ['Generic email Template for Installation letter'],
              message: 'Email template processing failed',
            },
            status: 400,
          }),
      ),
    );

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.nativeElement.click();

    fixture.detectChanges();

    expect(hostElement.querySelector('h2').textContent.trim()).toEqual(
      'Sorry, there was a problem when evaluating the email template "Generic email Template for Installation letter": Email template processing failed',
    );
  });

  it('should display error message when template NOTIF1001 error returns from api', () => {
    component.form.get('assignees').setValue(mockAssignees[0].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);

    jest.spyOn(tasksService, 'processRequestTaskAction').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'NOTIF1001',
              data: ['doctemplate.docx'],
              message: 'File does not exist for document template',
            },
            status: 400,
          }),
      ),
    );

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.nativeElement.click();

    fixture.detectChanges();

    expect(hostElement.querySelector('h2').textContent.trim()).toEqual(
      'Sorry, there was a problem when evaluating the document template "doctemplate.docx": File does not exist for document template',
    );
  });

  it('should display error message when template NOTIF1002 error returns from api', () => {
    component.form.get('assignees').setValue(mockAssignees[0].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);

    jest.spyOn(tasksService, 'processRequestTaskAction').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'NOTIF1002',
              data: ['doctemplate.docx'],
              message: 'Document template file generation failed',
            },
            status: 400,
          }),
      ),
    );

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.nativeElement.click();

    fixture.detectChanges();

    expect(hostElement.querySelector('h2').textContent.trim()).toEqual(
      'Sorry, there was a problem when evaluating the document template "doctemplate.docx": Document template file generation failed',
    );
  });

  it('should display error message when template NOTIF1003 error returns from api', () => {
    component.form.get('assignees').setValue(mockAssignees[0].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);

    jest.spyOn(tasksService, 'processRequestTaskAction').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'NOTIF1003',
              data: ['Generic email Template for Installation letter'],
              message: 'Email template does not exist',
            },
            status: 400,
          }),
      ),
    );

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.nativeElement.click();

    fixture.detectChanges();

    expect(hostElement.querySelector('h2').textContent.trim()).toEqual(
      'Sorry, there was a problem when evaluating the email template "Generic email Template for Installation letter": Email template does not exist',
    );
  });

  it('should prepopulate the "notify operator" form with the value of the currect user if it is an operator', () => {
    component.form.get('assignees').setValue(mockAssignees[3].id);
    component.form.get('users').setValue([mockUsers.authorities[0].userId]);

    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const assignees = component.form.get('assignees');
      const users = component.form.get('users');

      expect(assignees.value).toEqual(users.value);
    });
  });
});
