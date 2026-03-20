import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { Bdrs2ReturnForAmendsComponent } from './return-for-amends.component';

describe('Bdrs2ReturnForAmendsComponent', () => {
  let component: Bdrs2ReturnForAmendsComponent;
  let fixture: ComponentFixture<Bdrs2ReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<Bdrs2ReturnForAmendsComponent> {
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
      imports: [SharedModule],
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
            bdrs2SectionsCompleted: { baseline: false },
            regulatorReviewGroupDecisions: {
              BDRS2: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                } as any,
                reviewDataType: 'BDRS2_DATA',
              },
            } as any,
            regulatorReviewSectionsCompleted: {},
          },
        } as any,
      },
    });

    fixture = TestBed.createComponent(Bdrs2ReturnForAmendsComponent);
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
      requestTaskActionType: 'BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD',
        bdrs2SectionsCompleted: { baseline: false },
      },
    });
  });
});
