import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ResponseComponent } from '@tasks/doal/authority-response/response/response.component';
import { DoalAuthorityTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import { DoalAuthorityResponseRequestTaskPayload, RequestTaskPayload, TasksService } from 'pmrv-api';

describe('ResponseComponent', () => {
  let component: ResponseComponent;
  let fixture: ComponentFixture<ResponseComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub();

  class Page extends BasePage<ResponseComponent> {
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
    fixture = TestBed.createComponent(ResponseComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResponseComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new authority response', () => {
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
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
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

    it('should submit and navigate to preliminary allocations', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      const date = moment().format('YYYY-MM-DD').split('-');
      page.dateYear = date[0];
      page.dateMonth = date[1];
      page.dateDay = date[2];

      page.radioButtons[0].click();

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
            authorityResponse: {
              type: 'VALID',
              authorityRespondDate: new Date(component.form.get('authorityRespondDate').value),
              preliminaryAllocations: (
                mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                  .payload as DoalAuthorityResponseRequestTaskPayload
              ).regulatorPreliminaryAllocations,
            },
          },
          doalSectionsCompleted: {
            authorityResponse: false,
          } as { [key in DoalAuthorityTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['preliminary-allocations'], { relativeTo: activatedRoute });
    });
  });

  describe('for edit authority response', () => {
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

      const date = moment().format('YYYY-MM-DD').split('-');
      page.dateYear = date[0];
      page.dateMonth = date[1];
      page.dateDay = date[2];

      page.radioButtons[2].click();
      page.rejectedDecisionNotice = 'Rejected decision notice';

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
            ...(
              mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                .payload as DoalAuthorityResponseRequestTaskPayload
            ).doalAuthority,
            authorityResponse: {
              type: 'INVALID',
              authorityRespondDate: new Date(component.form.get('authorityRespondDate').value),
              decisionNotice: 'Rejected decision notice',
            },
          },
          doalSectionsCompleted: {
            dateSubmittedToAuthority: true,
            authorityResponse: false,
          } as { [key in DoalAuthorityTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
