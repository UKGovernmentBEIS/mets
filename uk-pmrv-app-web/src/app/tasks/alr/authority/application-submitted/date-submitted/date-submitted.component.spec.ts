import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { alrMockAuthorityPayload, alrMockAuthorityState } from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { addDays, format } from 'date-fns';

import { RequestTaskActionProcessDTO, TasksService } from 'pmrv-api';

import { AlrAuthorityDateSubmittedComponent } from './date-submitted.component';

describe('DateSubmittedComponent', () => {
  let component: AlrAuthorityDateSubmittedComponent;
  let fixture: ComponentFixture<AlrAuthorityDateSubmittedComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrAuthorityDateSubmittedComponent> {
    get dateDay() {
      return this.getInputValue('#submissionDate-day');
    }
    set dateDay(value: string) {
      this.setInputValue('#submissionDate-day', value);
    }

    get dateMonth() {
      return this.getInputValue('#submissionDate-month');
    }
    set dateMonth(value: string) {
      this.setInputValue('#submissionDate-month', value);
    }

    get dateYear() {
      return this.getInputValue('#submissionDate-year');
    }
    set dateYear(value: string) {
      this.setInputValue('#submissionDate-year', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAuthorityDateSubmittedComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockAuthorityState);

    fixture = TestBed.createComponent(AlrAuthorityDateSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate form and display an error message', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Enter a date']);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();

    const today = format(addDays(new Date(), 26), 'yyyy-MM-dd');
    const date = today.split('-');

    page.dateYear = date[0];
    page.dateMonth = date[1];
    page.dateDay = date[2];

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList[0]).toContain('This date must be the same as or before');
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should submit and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const [dateYear, dateMonth, dateDay] = format(new Date(), 'yyyy-MM-dd').split('-');
    page.dateYear = dateYear;
    page.dateMonth = dateMonth;
    page.dateDay = dateDay;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_SAVE_AUTHORITY_RESPONSE',
      requestTaskId: alrMockAuthorityState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
        authorityReviewOutcome: {
          ...alrMockAuthorityPayload.authorityReviewOutcome,
          submissionDate: component.form.get('submissionDate').value,
        },
        authorityReviewSectionsCompleted: { applicationSubmitted: false },
      },
    } as RequestTaskActionProcessDTO);

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], { relativeTo: activatedRoute });
  });
});
