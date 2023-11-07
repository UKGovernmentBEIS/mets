import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceTaskComponent } from '../../shared/components/non-compliance-task/non-compliance-task.component';
import { mockCompletedNonComplianceApplicationSubmitRequestTaskItem } from '../../test/mock';
import { DetailsOfBreachComponent } from './details-of-breach.component';

describe('DetailsOfBreachComponent', () => {
  let component: DetailsOfBreachComponent;
  let fixture: ComponentFixture<DetailsOfBreachComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DetailsOfBreachComponent> {
    get reason() {
      return this.fixture.componentInstance.form.get('reason');
    }

    get nonComplianceDate() {
      return this.getInputValue('#nonComplianceDate');
    }
    set nonComplianceDate(value: string) {
      this.setInputValue('#nonComplianceDate', value);
    }

    get complianceDate() {
      return this.getInputValue('#complianceDate');
    }
    set complianceDate(value: string) {
      this.setInputValue('#complianceDate', value);
    }

    get comments() {
      return this.getInputValue('#comments');
    }
    set comments(value: string) {
      this.setInputValue('#comments', value);
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
    fixture = TestBed.createComponent(DetailsOfBreachComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsOfBreachComponent, NonComplianceTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }, DestroySubject],
    }).compileComponents();
  });

  describe('for new non compliance', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask,
            payload: undefined,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to choose workflow', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['You must select a reason']);

      page.reason.setValue('FAILURE_TO_SURRENDER_ALLOWANCES');
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'NON_COMPLIANCE_SAVE_APPLICATION',
        requestTaskId: mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'NON_COMPLIANCE_SAVE_APPLICATION_PAYLOAD',
          reason: 'FAILURE_TO_SURRENDER_ALLOWANCES',
          sectionCompleted: false,
          nonComplianceDate: null,
          complianceDate: null,
          comments: null,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'choose-workflow'], { relativeTo: route });
    });
  });
});
