import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { alrMockReviewApplyPayload, alrMockReviewState, mockAlrReviewPostBuild } from '@tasks/alr/test/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrEstimatesComponent } from './estimates.component';

describe('AlrEstimatesComponent', () => {
  let component: AlrEstimatesComponent;
  let fixture: ComponentFixture<AlrEstimatesComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<AlrEstimatesComponent> {
    get areConservativeEstimatesRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="conservativeDeterminesActivity"]');
    }

    get explainEstimatesTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="conservativeDeterminesActivityComment"]');
    }

    get explainEstimates(): string {
      return this.getInputValue('#conservativeDeterminesActivityComment');
    }
    set explainEstimates(value: string) {
      this.setInputValue('#conservativeDeterminesActivityComment', value);
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
    fixture = TestBed.createComponent(AlrEstimatesComponent);
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

  describe('for new estimates', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(alrMockReviewState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.explainEstimatesTextArea).toBeDisabled();

      page.areConservativeEstimatesRadios[0].click();
      fixture.detectChanges();
      expect(page.explainEstimatesTextArea).not.toBeDisabled();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a comment']);

      page.explainEstimates = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockAlrReviewPostBuild(
          {
            regulatorReviewOutcome: {
              ...alrMockReviewApplyPayload.regulatorReviewOutcome,
              conservativeDeterminesActivity: true,
              conservativeDeterminesActivityComment: 'A comment',
            },
          },
          {
            ALC: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'preliminary-allocations'], { relativeTo: activatedRoute });
    });
  });
});
