import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AlrService } from '@tasks/alr/core';
import { alrMockReviewApplyPayload, alrMockReviewState, mockAlrReviewPostBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrActivityLevelDeleteComponent } from './activity-level-delete.component';

describe('ActivityLevelDeleteComponent', () => {
  let component: AlrActivityLevelDeleteComponent;
  let fixture: ComponentFixture<AlrActivityLevelDeleteComponent>;
  let store: CommonTasksStore;
  let router: Router;
  let page: Page;
  let activatedRoute: ActivatedRoute;

  const indexNo = '0';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {
      taskId: 1,
      index: indexNo,
    },
    null,
    { sectionKey: 'activityLevelChangeInformation' },
  );

  class Page extends BasePage<AlrActivityLevelDeleteComponent> {
    get deleteButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActivityLevelDeleteComponent],
      providers: [
        AlrService,
        CapitalizeFirstPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrActivityLevelDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.deleteButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrReviewPostBuild(
        {
          regulatorReviewOutcome: {
            ...alrMockReviewApplyPayload.regulatorReviewOutcome,
            historicalActivityLevels: alrMockReviewApplyPayload.regulatorReviewOutcome.historicalActivityLevels,
            activityLevels: alrMockReviewApplyPayload.regulatorReviewOutcome.activityLevels.filter(
              (_, i) => i !== +indexNo,
            ),
          },
        },
        {
          ...alrMockReviewApplyPayload.regulatorReviewSectionsCompleted,
          ALC: false,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
