import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SurrenderFinalAlrComponent } from './final-alr.component';

describe('FinalAlrComponent', () => {
  let component: SurrenderFinalAlrComponent;
  let fixture: ComponentFixture<SurrenderFinalAlrComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<SurrenderFinalAlrComponent> {
    get alrRequiredRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="alrRequired"]');
    }

    get alrReportDateDay() {
      return this.getInputValue('#alrReportDate-day');
    }
    set alrReportDateDay(value: string) {
      this.setInputValue('#alrReportDate-day', value);
    }

    get alrReportDateMonth() {
      return this.getInputValue('#alrReportDate-month');
    }
    set alrReportDateMonth(value: string) {
      this.setInputValue('#alrReportDate-month', value);
    }

    get alrReportDateYear() {
      return this.getInputValue('#alrReportDate-year');
    }
    set alrReportDateYear(value: string) {
      this.setInputValue('#alrReportDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SurrenderFinalAlrComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurrenderFinalAlrComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render form when data exists', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        ...mockTaskState.reviewDetermination,
        alrRequired: true,
        alrReportDate: '2030-12-21',
      } as PermitSurrenderReviewDeterminationGrant,
    });

    createComponent();
    fixture.detectChanges();

    expect(page.alrReportDateDay.trim()).toEqual('21');
    expect(page.alrReportDateMonth.trim()).toEqual('12');
    expect(page.alrReportDateYear.trim()).toEqual('2030');
  });

  it('should validate future date upon submitting', () => {
    store.setState(mockTaskState);
    createComponent();

    page.alrRequiredRadios[0].click();
    fixture.detectChanges();

    page.alrReportDateDay = '21';
    page.alrReportDateMonth = '12';
    page.alrReportDateYear = `${new Date(new Date().setFullYear(new Date().getFullYear() - 1))}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should submit upon report not required', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.alrRequiredRadios[1].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          ...mockTaskState.reviewDetermination,
          alrRequired: false,
          alrReportDate: null,
        },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances'], { relativeTo: route });
  });

  it('should submit upon valid date', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.alrRequiredRadios[0].click();
    fixture.detectChanges();

    page.alrReportDateDay = '21';
    page.alrReportDateMonth = '12';
    page.alrReportDateYear = '2030';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          ...mockTaskState.reviewDetermination,
          alrRequired: true,
          alrReportDate: new Date('2030-12-21'),
        },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances'], { relativeTo: route });
  });
});
