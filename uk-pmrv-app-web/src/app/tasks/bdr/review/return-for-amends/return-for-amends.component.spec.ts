import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { ReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let component: ReturnForAmendsComponent;
  let fixture: ComponentFixture<ReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ReturnForAmendsComponent> {
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
      imports: [BdrTaskSharedModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            regulatorReviewGroupDecisions: {
              BDR: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                } as any,
                reviewDataType: 'BDR_DATA',
              },
            } as any,
            regulatorReviewSectionsCompleted: {},
          },
        } as any,
      },
    });

    fixture = TestBed.createComponent(ReturnForAmendsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends, submit and navigate to confirmation', () => {
    expect(page.summary).toEqual([
      ['Changes required', '1. 234234'],
      ['Notes', 'srfs'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'BDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
