import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DateSubmittedComponent } from '@tasks/doal/authority-response/date-submitted/date-submitted.component';
import { DoalAuthorityTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';
import moment from 'moment/moment';

import { RequestTaskPayload, TasksService } from 'pmrv-api';

describe('DateSubmittedComponent', () => {
  let component: DateSubmittedComponent;
  let fixture: ComponentFixture<DateSubmittedComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub();

  class Page extends BasePage<DateSubmittedComponent> {
    get dateDay() {
      return this.getInputValue('#date-day');
    }
    set dateDay(value: string) {
      this.setInputValue('#date-day', value);
    }

    get dateMonth() {
      return this.getInputValue('#date-month');
    }
    set dateMonth(value: string) {
      this.setInputValue('#date-month', value);
    }

    get dateYear() {
      return this.getInputValue('#date-year');
    }
    set dateYear(value: string) {
      this.setInputValue('#date-year', value);
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
    fixture = TestBed.createComponent(DateSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DateSubmittedComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new date', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              doalAuthority: null,
              doalSectionsCompleted: {},
            } as RequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should validate form and display an error message', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a date']);
      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();

      const today = moment().add(26, 'd').format('YYYY-MM-DD');
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

      const date = moment().format('YYYY-MM-DD').split('-');
      page.dateYear = date[0];
      page.dateMonth = date[1];
      page.dateDay = date[2];

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_AUTHORITY_RESPONSE',
        requestTaskId: mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
          doalAuthority: {
            dateSubmittedToAuthority: {
              date: new Date(component.form.get('date').value),
            },
          },
          doalSectionsCompleted: {
            dateSubmittedToAuthority: false,
          } as { [key in DoalAuthorityTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for edit date', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
