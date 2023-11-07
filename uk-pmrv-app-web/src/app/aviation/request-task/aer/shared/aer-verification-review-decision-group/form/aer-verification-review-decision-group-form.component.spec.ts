import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { AerVerificationReviewDecisionGroupFormProvider } from '../aer-verification-review-decision-group-form-provider';
import { AerVerificationReviewDecisionGroupFormComponent } from './aer-verification-review-decision-group-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: ` <form [formGroup]="form">
    <app-aviation-aer-verification-review-decision-group-form></app-aviation-aer-verification-review-decision-group-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, AerVerificationReviewDecisionGroupFormComponent],
  providers: [AerVerificationReviewDecisionGroupFormProvider],
})
class MockParentComponent {
  form = this.formProvider.form;
  constructor(readonly formProvider: AerVerificationReviewDecisionGroupFormProvider, public store: RequestTaskStore) {
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
          daysRemaining: 6,
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: {
            payloadType: 'AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD',
            reviewSectionsCompleted: {},
          },
        },
        requestInfo: {
          type: 'AVIATION_AER_UKETS',
        },
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: true,
    } as any);
  }
}

describe('AerVerificationReviewDecisionGroupFormComponent', () => {
  it('should give the user the option to select decision type', async () => {
    await setup();
    expect(acceptedOption()).toBeInTheDocument();
  });

  function acceptedOption() {
    return screen.getByRole('radio', { name: /Accepted/ });
  }
});
