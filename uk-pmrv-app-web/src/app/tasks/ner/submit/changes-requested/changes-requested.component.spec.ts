import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerPostBuild, nerMockReviewPayload, nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { NerChangesRequestedComponent } from './changes-requested.component';

describe('NerChangesRequestedComponent', () => {
  let component: NerChangesRequestedComponent;
  let fixture: ComponentFixture<NerChangesRequestedComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<NerChangesRequestedComponent> {
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
      imports: [NerChangesRequestedComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        CapitalizeFirstPipe,
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...nerReviewMockState,
      requestTaskItem: {
        ...nerReviewMockState.requestTaskItem,
        allowedRequestTaskActions: ['NER_APPLICATION_AMENDS_SAVE'],
        requestTask: {
          ...nerReviewMockState.requestTaskItem.requestTask,
          type: 'NER_APPLICATION_AMENDS_SUBMIT',
          payload: {
            ...nerMockReviewPayload,
            payloadType: 'NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            nerSectionsCompleted: {
              NER: true,
            },
            regulatorReviewGroupDecisions: {
              NER: {
                reviewDataType: 'NER_DATA',
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
          } as NERApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(NerChangesRequestedComponent);
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
      mockNerPostBuild(
        {
          ner: {
            mmpFiles: undefined,
            nerFiles: undefined,
            notes: undefined,
          },
          regulatorReviewSectionsCompleted: {},
        },
        { NER: true, changesRequested: true },
        'NER_APPLICATION_AMENDS_SAVE',
        'NER_APPLICATION_AMENDS_SAVE_PAYLOAD',
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: route });
  });
});
