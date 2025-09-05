import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import {
  mockAlrAuthorityCompletedPayload,
  mockAlrAuthorityPostBuild,
  mockAlrAuthorityStateBuild,
} from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrApprovedAllocationsComponent } from './approved-allocations.component';

describe('AlrApprovedAllocationsComponent', () => {
  let component: AlrApprovedAllocationsComponent;
  let fixture: ComponentFixture<AlrApprovedAllocationsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrApprovedAllocationsComponent> {
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((tr) =>
        Array.from(tr.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(AlrApprovedAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
      imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrAuthorityStateBuild({
        ...(mockAlrAuthorityCompletedPayload as any),
      }),
    );
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.tableValues).toEqual([[], ['2023', '100'], ['2024', '200']]);
  });

  it('should submit and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrAuthorityPostBuild(
        {
          authorityReviewOutcome: mockAlrAuthorityCompletedPayload.authorityReviewOutcome,
        },
        {
          applicationSubmitted: true,
          authorityResponse: false,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
      relativeTo: route,
      state: { enableViewSummary: true },
    });
  });
});
