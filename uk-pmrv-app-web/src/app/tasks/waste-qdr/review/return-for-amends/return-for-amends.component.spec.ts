import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockWasteQdrReviewStateBuild } from '@tasks/waste-qdr/test/mock-review';
import { BasePage, mockClass } from '@testing';

import {
  TasksService,
  WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
} from 'pmrv-api';

import { WasteQdrReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let component: WasteQdrReturnForAmendsComponent;
  let fixture: ComponentFixture<WasteQdrReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<WasteQdrReturnForAmendsComponent> {
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
      imports: [WasteQdrReturnForAmendsComponent],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockWasteQdrReviewStateBuild({
        reviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'A note',
            requiredChanges: [
              { reason: 'Reason 1', files: ['65092804-17c9-41a8-9ee0-4e728046bb3d'] },
              { reason: 'Reason 2' },
            ],
          } as WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
        },
        regulatorReviewAttachments: { '65092804-17c9-41a8-9ee0-4e728046bb3d': 'testFile.txt' },
      } as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload),
    );

    fixture = TestBed.createComponent(WasteQdrReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends summary, submit and navigate to confirmation', () => {
    expect(page.summary).toEqual([
      ['Changes required', '1. Reason 1  testFile.txt  2. Reason 2'],
      ['Notes', 'A note'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'WASTE_QDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
