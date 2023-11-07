import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestTaskPayload, TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../store';
import { AccountClosureSubmitComponent } from './account-closure-submit.component';

describe('AccountClosureSubmitComponent', () => {
  let component: AccountClosureSubmitComponent;
  let fixture: ComponentFixture<AccountClosureSubmitComponent>;
  let store: RequestTaskStore;
  const backlinkService = mockClass(BackLinkService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });
  let page: Page;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AccountClosureSubmitComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AccountClosureSubmitComponent],
      providers: [
        { provide: BackLinkService, useValue: backlinkService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    fixture = TestBed.createComponent(AccountClosureSubmitComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct header', async () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to close this account?');
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_ACCOUNT_CLOSURE_SUBMIT_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
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
