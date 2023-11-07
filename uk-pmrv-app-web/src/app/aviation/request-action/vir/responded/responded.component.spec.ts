import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { RespondedComponent } from '@aviation/request-action/vir/responded/responded.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

describe('RespondedComponent', () => {
  let component: RespondedComponent;
  let fixture: ComponentFixture<RespondedComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const govukDate = new GovukDatePipe();
  const currentDate = new Date().toISOString();
  const currentDateText = govukDate.transform(currentDate, 'datetime');
  class Page extends BasePage<RespondedComponent> {
    get heading() {
      return `${this.query<HTMLHeadingElement>(
        'app-request-action-heading h1',
      ).textContent.trim()} ${this.query<HTMLHeadingElement>('app-request-action-heading p').textContent.trim()}`;
    }
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RespondedComponent, SharedModule, RequestActionTaskComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
        creationDate: currentDate,
        payload: {
          verifierUncorrectedItem: {
            reference: 'B1',
            explanation: 'Non conformity B1',
            materialEffect: false,
          },
          regulatorImprovementResponse: {
            operatorActions: 'Actions B1',
            improvementComments: 'Comments on the improvement B1',
            improvementDeadline: '2023-12-01',
            improvementRequired: true,
          },
          operatorImprovementResponse: {
            isAddressed: true,
            addressedDate: '2023-01-01',
            addressedDescription: 'Is addressed',
            uploadEvidence: true,
            files: ['c2221e49-b12d-460d-a1fa-9a2cd7ba811c'],
          },
          operatorImprovementFollowUpResponse: {
            improvementCompleted: true,
            dateCompleted: '2022-02-01',
          },
          virAttachments: {
            'c2221e49-b12d-460d-a1fa-9a2cd7ba811c': 'test-file.png',
          },
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(RespondedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the details', () => {
    expect(page.heading).toEqual(`Follow up response to B1 ${currentDateText}`);
    expect(page.summaryValues).toEqual([
      ['Item code', 'B1: an uncorrected error in the monitoring plan'],
      ["Verifier's recommendation", 'Non conformity B1'],
      ['Material?', 'No'],
      ['Addressed?', 'Yes - has been addressed or will be in the future'],
      ['Tell us how this recommendation will be (or has been) addressed', 'Is addressed'],
      ['Date of improvement', '1 Jan 2023'],
      ['Evidence uploaded?', 'Yes'],
      ['Uploaded files', 'test-file.png'],
      ["Regulator's decision", 'Improvement is required'],
      ["Regulator's comments", 'Comments on the improvement B1'],
      ['Actions for the operator', 'Actions B1'],
      ['Deadline for improvement', '1 Dec 2023'],
      ['Improvement complete?', 'Yes'],
      ['Date of improvement completion', '1 Feb 2022'],
    ]);
  });
});
