import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { SharedModule } from '@shared/shared.module';
import { nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { NerReturnForAmendsComponent } from './return-for-amends.component';

describe('NerReturnForAmendsComponent', () => {
  let component: NerReturnForAmendsComponent;
  let fixture: ComponentFixture<NerReturnForAmendsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NerReturnForAmendsComponent> {
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
      providers: [{ provide: TasksService, useValue: tasksService }, CapitalizeFirstPipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...nerReviewMockState,
      requestTaskItem: {
        ...nerReviewMockState.requestTaskItem,
        requestTask: {
          ...nerReviewMockState.requestTaskItem.requestTask,
          payload: {
            ...nerReviewMockState.requestTaskItem.requestTask.payload,
            nerSectionsCompleted: { NER: false },
            regulatorReviewGroupDecisions: {
              NER: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                } as any,
                reviewDataType: 'NER_DATA',
              },
            } as any,
            regulatorReviewSectionsCompleted: {},
          },
        } as any,
      },
    });

    fixture = TestBed.createComponent(NerReturnForAmendsComponent);
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
      requestTaskActionType: 'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD',
        nerSectionsCompleted: { NER: false },
      },
    });
  });
});
