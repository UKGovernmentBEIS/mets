import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import {
  inspectionForRespondMockPostBuild,
  inspectionRespondMockState,
  mockInspectionRespondRequestTaskPayload,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { FollowUpActionRespondComponent } from './follow-up-action.component';

describe('FollowUpActionComponent', () => {
  let component: FollowUpActionRespondComponent;
  let fixture: ComponentFixture<FollowUpActionRespondComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpActionRespondComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get completedRadios() {
      return this.queryAll<HTMLInputElement>('input[type="radio"]');
    }

    set explanationTrue(value: string) {
      this.setInputValue('#explanationTrue', value);
    }

    set explanationFalse(value: string) {
      this.setInputValue('#explanationFalse', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'onsite', actionId: '1' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpActionRespondComponent],
      providers: [
        TaskTypeToBreadcrumbPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionRespondMockState);

    fixture = TestBed.createComponent(FollowUpActionRespondComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and form with 0 errors', () => {
    expect(page.errorSummary).toBeFalsy();
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Follow-up action 2');
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.completedRadios[1].click();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
    expect(page.errorSummary).toBeTruthy();

    page.explanationFalse = 'New explanation';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      inspectionForRespondMockPostBuild(
        {
          followupActionsResponses: {
            ...mockInspectionRespondRequestTaskPayload.followupActionsResponses,
            1: {
              completed: false,
              explanation: 'New explanation',
              followUpActionResponseAttachments: [],
            },
          },
        },
        { 1: false },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: activatedRoute });
  });
});
