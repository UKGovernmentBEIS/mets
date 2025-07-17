import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { inspectionRespondMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { FollowUpSendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let component: FollowUpSendReportComponent;
  let fixture: ComponentFixture<FollowUpSendReportComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpSendReportComponent> {
    get content(): Array<string> {
      return this.queryAll<HTMLHeadingElement>('p').map((p) => p.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'onsite', actionId: '0' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpSendReportComponent],
      providers: [
        TaskTypeToBreadcrumbPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionRespondMockState);

    fixture = TestBed.createComponent(FollowUpSendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display content and submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.content[0]).toEqual('Your application will be sent directly to your Regulator (Environment Agency).');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
      requestTaskActionType: 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SUBMIT',
      requestTaskId: 1,
    });
  });
});
