import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem } from '../../test/mock';
import { ProvideDetailsComponent } from './provide-details.component';

describe('ProvideDetailsComponent', () => {
  let component: ProvideDetailsComponent;
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const expectedNextRoute = '../summary';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

  class Page extends BasePage<ProvideDetailsComponent> {
    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvideDetailsComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new allowance', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask,
            payload: undefined,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Enter a whole number',
        'Select a year',
        'Enter a comment',
        'Enter a date',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(4);
    });
  });

  describe('for completed payload', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask.payload,
              returnOfAllowances: {
                ...(
                  mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask
                    .payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload
                ).returnOfAllowances,
              },
            } as ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should submit a valid form and navigate to `summary` page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
