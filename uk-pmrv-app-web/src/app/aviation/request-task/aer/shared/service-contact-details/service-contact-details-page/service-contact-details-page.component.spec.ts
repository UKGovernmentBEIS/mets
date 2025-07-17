import { NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AerRequestTaskPayload, RequestTaskStore } from '../../../../store';
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

describe('ServiceContactDetailsPageComponent', () => {
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
    setupStore(store);

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

function setupStore(store: RequestTaskStore) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 1,
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
        daysRemaining: 6,
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          serviceContactDetails: {
            name: 'Din Djarin',
            roleCode: 'operator_admin',
            email: 'testing@testing.co.uk',
          },
          aerSectionsCompleted: {},
        } as AerRequestTaskPayload,
      },
      requestInfo: {
        id: '2',
        type: 'AVIATION_AER_UKETS',
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
