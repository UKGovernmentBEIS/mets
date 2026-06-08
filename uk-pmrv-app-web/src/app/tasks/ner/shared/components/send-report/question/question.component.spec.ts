import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { NerService } from '@tasks/ner/core';
import { nerMockReviewPayload, nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { NerSendReportQuestionComponent } from './question.component';

describe('NerSendReportQuestionComponent', () => {
  let component: NerSendReportQuestionComponent;
  let fixture: ComponentFixture<NerSendReportQuestionComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<NerSendReportQuestionComponent> {
    get needsVerification() {
      return this.query<HTMLInputElement>('#needsVerification-option0');
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
      imports: [NerSendReportQuestionComponent],
      providers: [
        NerService,
        CapitalizeFirstPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
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

    fixture = TestBed.createComponent(NerSendReportQuestionComponent);
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
    expect(page.errorSummaryListContents).toEqual(['Select an option']);
    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.needsVerification.click();

    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(navigateSpy).toHaveBeenCalledWith(['../'], {
      relativeTo: route,
      queryParams: { sendTo: 'verifier' },
    });
  });
});
