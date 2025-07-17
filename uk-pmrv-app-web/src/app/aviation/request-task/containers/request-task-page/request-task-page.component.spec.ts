import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import produce from 'immer';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../store';
import { RequestTaskPageComponent } from './request-task-page.component';
class Page extends BasePage<RequestTaskPageComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
}

describe('RequestTaskPageComponent', () => {
  let component: RequestTaskPageComponent;
  let fixture: ComponentFixture<RequestTaskPageComponent>;
  let store: RequestTaskStore;
  const backlinkService = mockClass(BackLinkService);
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [RequestTaskPageComponent],
      providers: [
        { provide: BackLinkService, useValue: backlinkService },
        { provide: ActivatedRoute, useValue: route },
        { provide: APP_BASE_HREF, useValue: '/installation-aviation/' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    fixture = TestBed.createComponent(RequestTaskPageComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct header', async () => {
    expect(page.header).toMatch(/^Apply for an emissions monitoring plan/);
    expect(page.header).toMatch(/Assigned to: TEST_ASSIGNEE$/);
  });

  it('should display correct header in wait for review', async () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW',
            assigneeFullName: 'TEST_ASSIGNEE',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(page.header).toMatch(/^Emissions monitoring plan application sent to regulator/);
    expect(page.header).toMatch(/Assigned to: TEST_ASSIGNEE$/);
  });

  it('should display correct header in variation wait for review', async () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW',
            assigneeFullName: 'TEST_ASSIGNEE',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(page.header).toMatch(/^Emissions monitoring plan variation sent to regulator/);
    expect(page.header).toMatch(/Assigned to: TEST_ASSIGNEE$/);
  });
});

function setupStore(store: RequestTaskStore) {
  store.setState({
    requestTaskItem: {
      allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_SAVE_APPLICATION'],
      requestTask: {
        id: 1,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          emissionsMonitoringPlan: {},
          empSectionsCompleted: {},
        } as EmpRequestTaskPayloadUkEts,
      },
      requestInfo: {
        id: '2',
        type: 'EMP_ISSUANCE_UKETS',
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
