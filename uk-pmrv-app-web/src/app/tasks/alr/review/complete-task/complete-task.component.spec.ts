import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { alrMockReviewState, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrCompleteTaskComponent } from './complete-task.component';

describe('CompleteTaskComponent', () => {
  let component: AlrCompleteTaskComponent;
  let fixture: ComponentFixture<AlrCompleteTaskComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrCompleteTaskComponent> {
    get confirmationContent() {
      return this.query<HTMLElement>('app-confirmation-shared');
    }

    get completeButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrCompleteTaskComponent],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {
            type: 'CLOSED_ALR',
            reason: 'A comment',
            alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
            files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
          },
        },
        regulatorReviewAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'testFile.txt' },
        regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: true },
      }),
    );

    fixture = TestBed.createComponent(AlrCompleteTaskComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.completeButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_CLOSE_APPLICATION',
      requestTaskId: 0,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    expect(page.confirmationContent).toBeTruthy();
  });
});
