import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

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
import { ChooseWorkflowComponent } from './choose-workflow.component';

describe('ChooseWorkflowComponent', () => {
  let component: ChooseWorkflowComponent;
  let fixture: ComponentFixture<ChooseWorkflowComponent>;

  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ChooseWorkflowComponent> {
    get itemListValues() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get addButton() {
      return this.query<HTMLButtonElement>('a[type="button"]');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ChooseWorkflowComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChooseWorkflowComponent, NonComplianceTaskComponent],
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

    it('should navigate to add item when add item button clicked', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.addButton.textContent.trim()).toEqual('Add an item');
    });
  });

  describe('for existing dre', () => {
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
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should navigate to add item when add item button clicked', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.addButton.textContent.trim()).toEqual('Add an item');
      expect(page.itemListValues.map((el) => el.textContent.trim())).toEqual(['AEM00094 Account creation']);
    });
  });
});
