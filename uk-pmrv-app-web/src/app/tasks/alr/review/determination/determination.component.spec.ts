import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrDeterminationComponent } from './determination.component';

describe('AlrDeterminationComponent', () => {
  let component: AlrDeterminationComponent;
  let fixture: ComponentFixture<AlrDeterminationComponent>;

  let router: Router;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {
      taskId: 1,
    },
    null,
    { sectionKey: 'determination' },
  );
  let activatedRoute: ActivatedRoute;

  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrDeterminationComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get proceedToAuthorityButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Proceed to UK ETS authority')[0];
    }

    get closeButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Close')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrDeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
      imports: [SharedModule, AlrTaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
        },
        regulatorReviewSectionsCompleted: { ALC: true },
      }),
    );
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('upon pressing proceed to authority button system invokes action and navigate to proceed page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.proceedToAuthorityButton.click();
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
              type: 'PROCEED_TO_AUTHORITY',
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
    expect(navigateSpy).toHaveBeenCalledWith(['proceed-authority', 'reason'], { relativeTo: activatedRoute });
  });

  it('upon pressing close button system invokes action and navigate to close page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.closeButton.click();
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
    expect(navigateSpy).toHaveBeenCalledWith(['close', 'reason'], { relativeTo: activatedRoute });
  });
});
