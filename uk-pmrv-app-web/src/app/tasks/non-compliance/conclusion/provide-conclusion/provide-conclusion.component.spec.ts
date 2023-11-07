import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceTaskComponent } from '../../shared/components/non-compliance-task/non-compliance-task.component';
import { mockCompletedNonComplianceConclusionRequestTaskItem } from '../../test/mock';
import { ProvideConclusionComponent } from './provide-conclusion.component';

describe('ProvideConclusionComponent', () => {
  let component: ProvideConclusionComponent;
  let fixture: ComponentFixture<ProvideConclusionComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const expectedNextRoute = '../summary';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

  class Page extends BasePage<ProvideConclusionComponent> {
    get complianceRestoredRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="complianceRestored"]');
    }

    get reissuePenaltyRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="reissuePenalty"]');
    }

    get operatorPaidRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="operatorPaid"]');
    }

    get comments() {
      return this.getInputValue('#comments');
    }
    set comments(value: string) {
      this.setInputValue('#comments', value);
    }

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
    fixture = TestBed.createComponent(ProvideConclusionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvideConclusionComponent, NonComplianceTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new conclusion', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedNonComplianceConclusionRequestTaskItem,
          requestTask: {
            ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask,
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
        'Select yes or no',
        'Please provide comments',
        'Select yes or no',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(3);
    });

    it('should submit a valid form and navigate to `summary` page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.complianceRestoredRadios[1].click();
      page.reissuePenaltyRadios[1].click();
      page.operatorPaidRadios[1].click();
      page.comments = 'some comments';
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
