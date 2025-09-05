import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AlrService } from '@tasks/alr/core';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let component: AlrReturnForAmendsComponent;
  let fixture: ComponentFixture<AlrReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrReturnForAmendsComponent> {
    get summary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrReturnForAmendsComponent],
      providers: [
        AlrService,
        CapitalizeFirstPipe,
        { provide: TasksService, useValue: tasksService },
        provideRouter([]),
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...alrMockReviewState,
      requestTaskItem: {
        ...alrMockReviewState.requestTaskItem,
        allowedRequestTaskActions: [
          ...alrMockReviewState.requestTaskItem.allowedRequestTaskActions,
          'ALR_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
        ],
        requestTask: {
          ...alrMockReviewState.requestTaskItem.requestTask,
          payload: {
            regulatorReviewGroupDecisions: {
              ALR: {
                reviewDataType: 'ALR_DATA',
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'Notes',
                  requiredChanges: [
                    { reason: 'Reason 1', files: ['65092804-17c9-41a8-9ee0-4e728046bb3d'] },
                    { reason: 'Reason 2' },
                  ],
                  verificationRequired: true,
                },
              },
            },
            regulatorReviewAttachments: { '65092804-17c9-41a8-9ee0-4e728046bb3d': 'testFile.txt' },
          } as unknown as ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(AlrReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends, submit and navigate to confirmation', () => {
    expect(page.summary).toEqual([
      ['Changes required from operator', '1. Reason 1  testFile.txt  2. Reason 2'],
      ['Notes', 'Notes'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
