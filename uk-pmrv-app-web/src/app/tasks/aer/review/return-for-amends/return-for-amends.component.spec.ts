import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { AerApplicationReviewRequestTaskPayload, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { AerSharedModule } from '../../shared/aer-shared.module';
import { mockState } from '../testing/mock-review';
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
      imports: [RouterTestingModule, SharedModule, AerSharedModule],
      declarations: [ReturnForAmendsComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
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
            reviewGroupDecisions: {
              EMISSIONS_SUMMARY: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                },
                reviewDataType: 'AER_DATA',
              },
            },
            reviewSectionsCompleted: {},
          } as AerApplicationReviewRequestTaskPayload,
        },
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
      requestTaskActionType: 'AER_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
