import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrReasonComponent } from './close-reason.component';

describe('AlrReasonComponent', () => {
  let component: AlrReasonComponent;
  let fixture: ComponentFixture<AlrReasonComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrReasonComponent> {
    get reasonTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="reason"]');
    }

    get reason(): string {
      return this.getInputValue('#reason');
    }
    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AlrTaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new reason', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockAlrReviewStateBuild({
          regulatorReviewOutcome: {
            ...(
              alrMockReviewState.requestTaskItem.requestTask
                .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewOutcome,
            determination: {
              type: 'CLOSED_ALR',
            },
          },
          regulatorReviewSectionsCompleted: { ALC: true },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reason = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockAlrReviewPostBuild(
          {
            regulatorReviewOutcome: {
              ...(
                alrMockReviewState.requestTaskItem.requestTask
                  .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
              )?.regulatorReviewOutcome,
              determination: {
                type: 'CLOSED_ALR',
                reason: 'A comment',
              },
            },
          },
          {
            ALC: true,
            DETERMINATION: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'latest-activity'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
