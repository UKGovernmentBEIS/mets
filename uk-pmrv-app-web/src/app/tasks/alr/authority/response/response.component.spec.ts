import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { alrMockAuthorityPayload, alrMockAuthorityState } from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { addDays, format } from 'date-fns';

import { TasksService } from 'pmrv-api';

import { AlrResponseComponent } from './response.component';

describe('AlrResponseComponent', () => {
  let component: AlrResponseComponent;
  let fixture: ComponentFixture<AlrResponseComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub();

  class Page extends BasePage<AlrResponseComponent> {
    get dateDay() {
      return this.getInputValue('#authorityRespondDate-day');
    }
    set dateDay(value: string) {
      this.setInputValue('#authorityRespondDate-day', value);
    }
    get dateMonth() {
      return this.getInputValue('#authorityRespondDate-month');
    }
    set dateMonth(value: string) {
      this.setInputValue('#authorityRespondDate-month', value);
    }
    get dateYear() {
      return this.getInputValue('#authorityRespondDate-year');
    }
    set dateYear(value: string) {
      this.setInputValue('#authorityRespondDate-year', value);
    }

    get radioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
    }

    get acceptedDecisionNotice() {
      return this.getInputValue('#acceptedDecisionNotice');
    }
    set acceptedDecisionNotice(value: string) {
      this.setInputValue('#acceptedDecisionNotice', value);
    }

    get rejectedDecisionNotice() {
      return this.getInputValue('#rejectedDecisionNotice');
    }
    set rejectedDecisionNotice(value: string) {
      this.setInputValue('#rejectedDecisionNotice', value);
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

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrResponseComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule],
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new authority response', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(alrMockAuthorityState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should validate form and display an error message', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a date', 'Select an option']);
      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();

      page.radioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a date', 'Enter a comment']);
      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();

      page.radioButtons[2].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a date', 'Enter a comment']);
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

    it('should submit and navigate to preliminary allocations', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      const [dateYear, dateMonth, dateDay] = format(new Date(), 'yyyy-MM-dd').split('-');
      page.dateYear = dateYear;
      page.dateMonth = dateMonth;
      page.dateDay = dateDay;

      page.radioButtons[0].click();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'ALR_SAVE_AUTHORITY_RESPONSE',
        requestTaskId: 1,
        requestTaskActionPayload: {
          payloadType: 'ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
          authorityReviewOutcome: {
            ...alrMockAuthorityPayload.authorityReviewOutcome,
            authorityResponse: {
              type: 'VALID',
              authorityRespondDate: new Date(component.form.get('authorityRespondDate').value),
            },
          },
          authorityReviewSectionsCompleted: {
            authorityResponse: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['preliminary-allocations'], { relativeTo: activatedRoute });
    });
  });
});
