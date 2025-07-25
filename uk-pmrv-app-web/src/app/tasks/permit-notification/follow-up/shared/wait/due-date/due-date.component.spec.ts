import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { addDays, format } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationWaitForFollowUpRequestTaskPayload, RequestActionsService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass, MockType } from '../../../../../../../testing';
import { TaskSharedModule } from '../../../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DueDateComponent } from './due-date.component';

describe('Due Date Component', () => {
  let component: DueDateComponent;
  let fixture: ComponentFixture<DueDateComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(null)),
  };
  const requestActionsService = mockClass(RequestActionsService);

  class Page extends BasePage<DueDateComponent> {
    get dueDateDay() {
      return this.getInputValue('#followUpResponseExpirationDate-day');
    }
    set dueDateDay(value: string) {
      this.setInputValue('#followUpResponseExpirationDate-day', value);
    }

    get dueDateMonth() {
      return this.getInputValue('#followUpResponseExpirationDate-month');
    }
    set dueDateMonth(value: string) {
      this.setInputValue('#followUpResponseExpirationDate-month', value);
    }

    get dueDateYear() {
      return this.getInputValue('#followUpResponseExpirationDate-year');
    }
    set dueDateYear(value: string) {
      this.setInputValue('#followUpResponseExpirationDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const followUpResponseExpirationDate = format(new Date(addDays(new Date(), 5).toISOString()), 'yyyy-MM-dd');

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Edit follow up response deadline',
    keys: ['followUpResponseExpirationDate'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(DueDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DueDateComponent],
      imports: [RouterTestingModule, TaskSharedModule, SharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD',
            followUpRequest: 'test',
            followUpResponseExpirationDate: followUpResponseExpirationDate,
          } as PermitNotificationWaitForFollowUpRequestTaskPayload,
        },
      },
    });
  });

  beforeEach(createComponent);

  afterEach(() => jest.clearAllMocks());

  it('should render form', () => {
    expect(component).toBeTruthy();
  });

  it('should validate form and display an error message', () => {
    page.dueDateYear = '';
    page.dueDateMonth = '';
    page.dueDateDay = '';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter a date']);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for minimum date limit', () => {
    const date = followUpResponseExpirationDate.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.dueDateYear = year;
    page.dueDateMonth = month;
    page.dueDateDay = days;
    page.submitButton.click();
    fixture.detectChanges();
    const dateMsg = format(new Date(followUpResponseExpirationDate), 'dd MMM yyyy');
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([`The date must be after ${dateMsg}`]);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('Should submit form and return to tasklist', () => {
    requestActionsService.getRequestActionsByRequestId.mockReturnValue(of([]));

    const navigateSpy = jest.spyOn(router, 'navigate');
    const timelineSpy = jest.spyOn(store, 'updateTimelineActions');
    const today = format(new Date(addDays(new Date(), 6).toISOString()), 'yyyy-MM-dd');

    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD',
            followUpRequest: 'test',
            followUpResponseExpirationDate: followUpResponseExpirationDate,
          } as PermitNotificationWaitForFollowUpRequestTaskPayload,
        },
        requestInfo: {
          id: '123',
        },
      },
    });

    page.dueDateYear = year;
    page.dueDateMonth = month;
    page.dueDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE',
      requestTaskId: 63,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD',
        dueDate: new Date(component.form.get('followUpResponseExpirationDate').value),
      },
    });
    expect(timelineSpy).toHaveBeenCalledWith('123');
    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route, state: { notification: true } });
  });
});
