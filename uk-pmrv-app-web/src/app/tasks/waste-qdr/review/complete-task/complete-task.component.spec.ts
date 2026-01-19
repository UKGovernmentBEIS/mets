import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockWasteQdrReviewStateBuild, wasteQdrMockReviewState } from '@tasks/waste-qdr/test/mock-review';
import { BasePage, mockClass } from '@testing';

import { TasksService, WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { WasteQdrCompleteTaskComponent } from './complete-task.component';

describe('CompleteTaskComponent', () => {
  let component: WasteQdrCompleteTaskComponent;
  let fixture: ComponentFixture<WasteQdrCompleteTaskComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<WasteQdrCompleteTaskComponent> {
    get confirmationContent() {
      return this.query<HTMLElement>('app-confirmation-shared');
    }

    get completeButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrCompleteTaskComponent],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockWasteQdrReviewStateBuild({
        reviewDecision: {
          ...(
            wasteQdrMockReviewState.requestTaskItem.requestTask
              .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.reviewDecision,
        },
        regulatorReviewAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'testFile.txt' },
        regulatorReviewSectionsCompleted: { qdr: true },
      }),
    );

    fixture = TestBed.createComponent(WasteQdrCompleteTaskComponent);
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
      requestTaskActionType: 'WASTE_QDR_REGULATOR_REVIEW_SUBMIT',
      requestTaskId: 0,
      requestTaskActionPayload: {
        decisionNotification: {
          externalContacts: [],
          operators: [],
          signatory: '80a57c50-1aaa-421f-9e1d-fdf3268cca8b',
        },
        payloadType: 'WASTE_QDR_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
      },
    });

    expect(page.confirmationContent).toBeTruthy();
  });
});
