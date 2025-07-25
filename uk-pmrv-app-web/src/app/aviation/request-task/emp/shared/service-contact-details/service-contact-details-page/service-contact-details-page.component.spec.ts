import { NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
} from '@aviation/request-task/store/request-task.types';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import ServiceContactDetailsPageComponent from './service-contact-details-page.component';

@NgModule()
export class FixNavigationTriggeredOutsideAngularZoneNgModule {
  // eslint-disable-next-line
  constructor(_router: Router) {}
}

class Page extends BasePage<ServiceContactDetailsPageComponent> {
  get body() {
    return this.query<HTMLElement>('.govuk-body');
  }
}

// UKETS
describe('ServiceContactDetailsPageComponent for UKETS', () => {
  let component: ServiceContactDetailsPageComponent;
  let fixture: ComponentFixture<ServiceContactDetailsPageComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, FixNavigationTriggeredOutsideAngularZoneNgModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store, true);
    fixture = TestBed.createComponent(ServiceContactDetailsPageComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('body should be correct', () => {
    expect(page.body.textContent).toContain(
      'You can change this information in the users and contacts section, but will not see the changes until you submit the application.',
    );
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    component.onSubmit();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
  });
});

// CORSIA
describe('ServiceContactDetailsPageComponent for CORSIA', () => {
  let component: ServiceContactDetailsPageComponent;
  let fixture: ComponentFixture<ServiceContactDetailsPageComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, FixNavigationTriggeredOutsideAngularZoneNgModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store, true);
    fixture = TestBed.createComponent(ServiceContactDetailsPageComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('body should be correct', () => {
    expect(page.body.textContent).toContain(
      'You can change this information in the users and contacts section, but will not see the changes until you submit the application.',
    );
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    component.onSubmit();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
  });
});

function setupStore(store: RequestTaskStore, isCorsia: boolean) {
  const payload = {
    emissionsMonitoringPlan: { operatorDetails: {} },
    serviceContactDetails: {
      name: 'Din Djarin',
      roleCode: 'operator_admin',
      email: 'testing@testing.co.uk',
    },
    empSectionsCompleted: {},
  };

  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 1,
        type: isCorsia ? 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT' : 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        daysRemaining: 6,
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: isCorsia ? (payload as EmpRequestTaskPayloadCorsia) : (payload as EmpRequestTaskPayloadUkEts),
      },
      requestInfo: {
        id: '2',
        accountId: 2,
        type: isCorsia ? 'EMP_ISSUANCE_CORSIA' : 'EMP_ISSUANCE_UKETS',
      },
    },
    timeline: [],
    relatedTasks: [
      {
        taskId: 2,
      },
    ],
    taskReassignedTo: null,
    isTaskReassigned: false,
    isEditable: true,
    tasksState: {},
  });
}
