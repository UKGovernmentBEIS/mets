import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { initialState } from '../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { NonComplianceTaskComponent } from '../../../shared/components/non-compliance-task/non-compliance-task.component';
import { mockCompletedNonComplianceApplicationSubmitRequestTaskItem } from '../../../test/mock';
import { ChooseWorkflowAddComponent } from './choose-workflow-add.component';

describe('ChooseWorkflowAddComponent', () => {
  let component: ChooseWorkflowAddComponent;
  let fixture: ComponentFixture<ChooseWorkflowAddComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ChooseWorkflowAddComponent> {
    get selectedRequests() {
      return this.fixture.componentInstance.form.get('selectedRequests');
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
    fixture = TestBed.createComponent(ChooseWorkflowAddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new non compliance', () => {
    const route = new ActivatedRouteStub({});
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [ChooseWorkflowAddComponent, NonComplianceTaskComponent],
        imports: [RouterTestingModule, SharedModule, TaskSharedModule],
        providers: [
          KeycloakService,
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: route },
        ],
      }).compileComponents();
    });

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

    it('should submit', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['You must add at least one item']);

      page.selectedRequests.setValue('NC00094-7');
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
          selectedRequests: ['NC00094-7'],
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../..', 'choose-workflow'], { relativeTo: route });
    });
  });
});
