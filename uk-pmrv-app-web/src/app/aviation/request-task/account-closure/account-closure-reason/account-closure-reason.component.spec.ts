import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { RequestTaskPayload } from 'pmrv-api';

import { RequestTaskStore } from '../../store';
import { AccountClosureReasonComponent } from './account-closure-reason.component';

class Page extends BasePage<AccountClosureReasonComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get taskHeader(): string {
    return this.query('app-task-header-info').textContent.trim();
  }
}

describe('AccountClosureReasonComponent', () => {
  let component: AccountClosureReasonComponent;
  let fixture: ComponentFixture<AccountClosureReasonComponent>;
  let store: RequestTaskStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AccountClosureReasonComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    fixture = TestBed.createComponent(AccountClosureReasonComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct header', async () => {
    expect(page.header).toMatch(/^Close Account/);
    expect(page.taskHeader).toMatch(/Assigned to: TEST_ASSIGNEE$/);
  });
});

function setupStore(store: RequestTaskStore) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 1,
        type: 'AVIATION_ACCOUNT_CLOSURE_SUBMIT',
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          payloadType: 'AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD',
        } as RequestTaskPayload,
      },
      requestInfo: {
        id: '2',
        type: 'AVIATION_ACCOUNT_CLOSURE',
      },
    },
    timeline: [],
    relatedTasks: [],
    taskReassignedTo: null,
    isTaskReassigned: false,
    isEditable: true,
    tasksState: {},
  });
}
