import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { mockAlrPostBuild } from '@tasks/alr/test/mock';
import { alrMockReviewApplyPayload, alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrChangesRequestedComponent } from './changes-requested.component';

describe('ChangesRequestedComponent', () => {
  let component: AlrChangesRequestedComponent;
  let fixture: ComponentFixture<AlrChangesRequestedComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<AlrChangesRequestedComponent> {
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrChangesRequestedComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...alrMockReviewState,
      requestTaskItem: {
        ...alrMockReviewState.requestTaskItem,
        allowedRequestTaskActions: ['ALR_APPLICATION_AMENDS_SAVE'],
        requestTask: {
          ...alrMockReviewState.requestTaskItem.requestTask,
          type: 'ALR_APPLICATION_AMENDS_SUBMIT',
          payload: {
            ...alrMockReviewApplyPayload,
            payloadType: 'ALR_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            alrSectionsCompleted: {
              activity: true,
            },
            regulatorReviewGroupDecisions: {
              ALR: {
                reviewDataType: 'ALR_DATA',
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'Notes',
                  requiredChanges: [
                    { reason: 'Reason 1', files: ['65092804-17c9-41a8-9ee0-4e728046bb3d'] },
                    { reason: 'Reason 2' },
                  ],
                  verificationRequired: true,
                },
              },
            },
            regulatorReviewAttachments: { '65092804-17c9-41a8-9ee0-4e728046bb3d': 'testFile.txt' },
          } as ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(AlrChangesRequestedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display error on empty form submit', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual([
      'Check the box to confirm you have made changes and want to mark as complete',
    ]);
    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should submit a valid form and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.checkboxes[0].click();

    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrPostBuild(
        {
          alr: { alrFile: '119f3261-69b2-435d-bb19-4545809c3864', files: ['119f3261-69b2-435d-bb19-4545809c3864'] },
          regulatorReviewSectionsCompleted: { ALC: false },
        },
        { changesRequested: true },
        'ALR_APPLICATION_AMENDS_SAVE',
        'ALR_APPLICATION_AMENDS_SAVE_PAYLOAD',
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: route });
  });
});
