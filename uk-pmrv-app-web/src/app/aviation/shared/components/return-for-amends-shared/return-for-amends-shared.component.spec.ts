import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { ReturnForAmendsSharedComponent } from './return-for-amends-shared.component';

describe('ReturnForAmendsSharedComponent', () => {
  let page: Page;
  let component: ReturnForAmendsSharedComponent;
  let fixture: ComponentFixture<ReturnForAmendsSharedComponent>;

  class Page extends BasePage<ReturnForAmendsSharedComponent> {
    get summary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  const tasksService = mockClass(TasksService);

  const mockDecisionAmends = [
    {
      groupKey: 'emissionsReductionClaim',
      data: {
        type: 'OPERATOR_AMENDS_NEEDED',
        details: {
          notes: 'test note',
          requiredChanges: [
            {
              reason: 'test reason',
              files: [],
            },
          ],
        },
      },
    },
  ];
  const mockRequestTaskType = 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW';

  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '123' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReturnForAmendsSharedComponent);
    component = fixture.componentInstance;
    component.decisionAmends = mockDecisionAmends;
    component.requestTaskType = mockRequestTaskType;
    page = new Page(fixture);
    fixture.detectChanges();

    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends, submit and display confirmation', () => {
    expect(page.summary).toEqual([['Changes required', '1. test reason']]);

    page.submitButton.click();
    component.isSubmitted$ = new BehaviorSubject(true);
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 123,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    expect(
      screen.getByText(/The application has been returned to the operator so they can make the necessary changes./),
    ).toBeTruthy();
  });
});
