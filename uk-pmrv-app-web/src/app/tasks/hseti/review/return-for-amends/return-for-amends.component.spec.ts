import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { HsetiReturnForAmendsComponent } from './return-for-amends.component';

describe('HsetiReturnForAmendsComponent', () => {
  let component: HsetiReturnForAmendsComponent;
  let fixture: ComponentFixture<HsetiReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<HsetiReturnForAmendsComponent> {
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
      imports: [HseTiTaskSharedModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...hsetiMockReviewState,
      requestTaskItem: {
        ...hsetiMockReviewState.requestTaskItem,
        requestTask: {
          ...hsetiMockReviewState.requestTaskItem.requestTask,
          payload: {
            ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
            regulatorReviewGroupDecisions: {
              HSETI: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                } as any,
              },
            } as any,
            regulatorReviewSectionsCompleted: {},
          },
        } as any,
      },
    });

    fixture = TestBed.createComponent(HsetiReturnForAmendsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends, submit and navigate to confirmation', () => {
    expect(page.summary).toEqual([
      ['Changes required from operator', '1. 234234'],
      ['Notes', 'srfs'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'HSE_TI_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
