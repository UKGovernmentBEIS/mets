import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem } from '../../test/mock';
import { ProvideReturnedDetailsComponent } from './provide-returned-details.component';

describe('ProvideReturnedDetailsComponent', () => {
  let component: ProvideReturnedDetailsComponent;
  let fixture: ComponentFixture<ProvideReturnedDetailsComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ProvideReturnedDetailsComponent> {
    get isAllowancesReturnedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="isAllowancesReturned"]');
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
    fixture = TestBed.createComponent(ProvideReturnedDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvideReturnedDetailsComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new non returned', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask,
            payload: undefined,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to summary', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select an option']);

      page.isAllowancesReturnedRadios[1].click();
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION',
        requestTaskId: mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION_PAYLOAD',
          returnOfAllowancesReturned: {
            isAllowancesReturned: false,
            returnedAllowancesDate: null,
            regulatorComments: null,
          },
          sectionsCompleted: {
            PROVIDE_RETURNED_DETAILS: false,
          },
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route });
    });
  });
});
