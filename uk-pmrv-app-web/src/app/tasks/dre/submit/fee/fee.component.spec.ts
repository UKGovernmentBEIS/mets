import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { FeeComponent } from './fee.component';

describe('FeeComponent', () => {
  let component: FeeComponent;
  let fixture: ComponentFixture<FeeComponent>;
  let hostElement: HTMLElement;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<FeeComponent> {
    get totalBillableHours() {
      return this.getInputValue('#totalBillableHours');
    }
    set totalBillableHours(value: string) {
      this.setInputValue('#totalBillableHours', value);
    }

    get hourlyRate() {
      return this.getInputValue('#hourlyRate');
    }
    set hourlyRate(value: string) {
      this.setInputValue('#hourlyRate', value);
    }

    get dueDateDay() {
      return this.getInputValue('#dueDate-day');
    }
    set dueDateDay(value: string) {
      this.setInputValue('#dueDate-day', value);
    }
    get dueDateMonth() {
      return this.getInputValue('#dueDate-month');
    }
    set dueDateMonth(value: string) {
      this.setInputValue('#dueDate-month', value);
    }
    get dueDateYear() {
      return this.getInputValue('#dueDate-year');
    }
    set dueDateYear(value: string) {
      this.setInputValue('#dueDate-year', value);
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
    fixture = TestBed.createComponent(FeeComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeeComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ fee: { chargeOperator: true } }, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and redirect to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.totalBillableHours = '5';
      page.hourlyRate = '20';

      const dueDate = moment().add(1, 'day').format('YYYY-MM-DD');
      const date = dueDate.split('-');
      page.dueDateYear = date[0];
      page.dueDateMonth = date[1];
      page.dueDateDay = date[2];

      fixture.detectChanges();

      expect(hostElement.textContent.trim()).toContain('Total operator fee: 100 £');

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            fee: {
              chargeOperator: true,
              feeDetails: {
                dueDate: new Date(dueDate),
                totalBillableHours: '5',
                hourlyRate: '20',
              },
            },
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: route });
    });
  });
});
