import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import {
  mockAlrAuthorityCompletedPayload,
  mockAlrAuthorityPostBuild,
  mockAlrAuthorityStateBuild,
} from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { ALRAuthorityResponseSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrResponseSummaryComponent } from './summary.component';

describe('AlrResponseSummaryComponent', () => {
  let component: AlrResponseSummaryComponent;
  let fixture: ComponentFixture<AlrResponseSummaryComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrResponseSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrResponseSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrAuthorityStateBuild(mockAlrAuthorityCompletedPayload as ALRAuthorityResponseSubmitRequestTaskPayload),
    );
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['When did the Authority respond?', '11 Feb 2024'],
      ['Authority decision', 'Approved with corrections'],
      ['Explanation of Authority decision for notice', '14545'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100'],
      ['2024', 'Aluminium', '200'],
      [],
      ['2023', '100'],
      ['2024', '200'],
    ]);
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrAuthorityPostBuild(
        {
          authorityReviewOutcome: mockAlrAuthorityCompletedPayload.authorityReviewOutcome,
        },
        {
          applicationSubmitted: true,
          authorityResponse: true,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
