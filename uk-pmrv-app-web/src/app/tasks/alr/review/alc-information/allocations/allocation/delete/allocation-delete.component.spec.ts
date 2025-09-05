import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrAllocationDeleteComponent } from './allocation-delete.component';

describe('AlrAllocationDeleteComponent', () => {
  let component: AlrAllocationDeleteComponent;
  let fixture: ComponentFixture<AlrAllocationDeleteComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {
      taskId: 1,
      index: '0',
    },
    null,
    { sectionKey: 'activityLevelChangeInformation' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrAllocationDeleteComponent> {
    get deleteButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrAllocationDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule],
      providers: [
        provideRouter([]),
        AlrService,
        CapitalizeFirstPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          allocations: [{ subInstallationName: 'ALUMINIUM', year: 2025, allowances: 10 }],
        },
      }),
    );
  });
  beforeEach(createComponent);

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
            allocations: [],
          },
        },
        {
          ALC: false,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
