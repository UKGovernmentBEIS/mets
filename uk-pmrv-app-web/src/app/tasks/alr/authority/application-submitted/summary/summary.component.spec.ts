import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import {
  alrMockAuthorityPayload,
  alrMockAuthorityState,
  mockAlrAuthorityStateBuild,
} from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestTaskActionProcessDTO, TasksService } from 'pmrv-api';

import { AlrAuthoritySummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: AlrAuthoritySummaryComponent;
  let fixture: ComponentFixture<AlrAuthoritySummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrAuthoritySummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('dl')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAuthoritySummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrAuthorityStateBuild({
        ...alrMockAuthorityPayload,
        authorityReviewOutcome: { ...alrMockAuthorityPayload.authorityReviewOutcome, submissionDate: '2024-12-11' },
      }),
    );

    fixture = TestBed.createComponent(AlrAuthoritySummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([
      ['When was the relevant information submitted to the authority?', '11 Dec 2024'],
    ]);
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_SAVE_AUTHORITY_RESPONSE',
      requestTaskId: alrMockAuthorityState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
        authorityReviewOutcome: { ...alrMockAuthorityPayload.authorityReviewOutcome, submissionDate: '2024-12-11' },
        authorityReviewSectionsCompleted: { applicationSubmitted: true },
      },
    } as RequestTaskActionProcessDTO);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
