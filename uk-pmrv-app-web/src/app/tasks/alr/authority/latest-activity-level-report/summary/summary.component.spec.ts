import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import {
  alrMockAuthorityPayload,
  mockAlrAuthorityPostBuild,
  mockAlrAuthorityStateBuild,
} from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrAuthorityuploadLatestAlrSummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: AlrAuthorityuploadLatestAlrSummaryComponent;
  let fixture: ComponentFixture<AlrAuthorityuploadLatestAlrSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrAuthorityuploadLatestAlrSummaryComponent> {
    get summaryTemplate() {
      return this.query('app-alr-activity-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAuthorityuploadLatestAlrSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrAuthorityStateBuild(
        {
          payloadType: 'ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD',
          authorityReviewOutcome: {
            ...alrMockAuthorityPayload.authorityReviewOutcome,
            alr: {
              alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
          },
          authorityReviewSectionsCompleted: { upload: false },
          alrAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
        },
        'ALR_AUTHORITY_RESPONSE_SUBMIT',
      ),
    );

    fixture = TestBed.createComponent(AlrAuthorityuploadLatestAlrSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and redirect to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.summaryTemplate).toBeTruthy();
    expect(page.submitButton).toBeTruthy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrAuthorityPostBuild(
        {
          authorityReviewOutcome: {
            ...alrMockAuthorityPayload.authorityReviewOutcome,
            alr: {
              alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
          },
        },
        { upload: true },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
