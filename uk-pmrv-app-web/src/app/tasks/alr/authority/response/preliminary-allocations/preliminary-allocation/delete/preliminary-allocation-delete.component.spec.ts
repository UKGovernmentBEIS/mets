import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { mockAlrAuthorityPostBuild, mockAlrAuthorityStateBuild } from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrPreliminaryAllocationDeleteComponent } from './preliminary-allocation-delete.component';

describe('AlrPreliminaryAllocationDeleteComponent', () => {
  let component: AlrPreliminaryAllocationDeleteComponent;
  let fixture: ComponentFixture<AlrPreliminaryAllocationDeleteComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {
      taskId: 1,
      index: '0',
    },
    null,
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrPreliminaryAllocationDeleteComponent> {
    get deleteButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrPreliminaryAllocationDeleteComponent);
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
      mockAlrAuthorityStateBuild({
        authorityReviewOutcome: {
          authorityResponse: {
            type: 'VALID_WITH_CORRECTIONS',
            decisionNotice: '14545',
            authorityRespondDate: '2024-02-11',
            preliminaryAllocations: [
              { subInstallationName: 'ALUMINIUM', year: 2025, allowances: 10, allocationId: '0' },
            ],
          },
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
      mockAlrAuthorityPostBuild(
        {
          authorityReviewOutcome: {
            authorityResponse: {
              type: 'VALID_WITH_CORRECTIONS',
              decisionNotice: '14545',
              authorityRespondDate: '2024-02-11',
              preliminaryAllocations: [],
            },
          },
        },
        {
          authorityResponse: false,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
