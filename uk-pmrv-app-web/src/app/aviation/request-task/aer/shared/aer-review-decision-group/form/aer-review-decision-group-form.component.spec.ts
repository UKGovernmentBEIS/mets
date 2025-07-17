import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { AerReviewDecisionGroupFormProvider } from '../aer-review-decision-group-form.provider';
import { AerReviewDecisionGroupFormComponent } from './aer-review-decision-group-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-aviation-aer-review-decision-group-form></app-aviation-aer-review-decision-group-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, AerReviewDecisionGroupFormComponent],
  providers: [AerReviewDecisionGroupFormProvider],
})
class MockParentComponent {
  form = this.formProvider.form;
  constructor(
    readonly formProvider: AerReviewDecisionGroupFormProvider,
    public store: RequestTaskStore,
  ) {
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

describe('AerReviewDecisionGroupFormComponent', () => {
  it('should give the user the option to select decision type', async () => {
    await setup();
    expect(acceptedOption()).toBeInTheDocument();
    expect(amendsOption()).toBeInTheDocument();
  });

  it(`should not have selected option`, async () => {
    await setup();
    expect(acceptedOption()).not.toBeChecked();
    expect(amendsOption()).not.toBeChecked();
  });

  it(`should show label 'Changes required by the operator' when user selects 'Operator amends needed'`, async () => {
    const { user, detectChanges } = await setup();
    await user.click(amendsOption());
    detectChanges();
    expect(screen.getByText(/Changes required by the operator/)).toBeInTheDocument();
  });

  it('should give the option to add more required changes', async () => {
    const { user, detectChanges } = await setup();
    await user.click(amendsOption());
    detectChanges();
    const addButton = screen.getByRole('button', { name: 'Add another required change' });
    expect(addButton).toBeInTheDocument();

    await user.click(addButton);
    detectChanges();
    expect(screen.getByText(/Required change 1/)).toBeInTheDocument();
    expect(screen.getByText(/Required change 2/)).toBeInTheDocument();
  });

  it('should give user option to remove any required change if more than one', async () => {
    const { user, detectChanges } = await setup();
    await user.click(amendsOption());
    detectChanges();
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: /Add another required change/ }));
    detectChanges();
    expect(screen.queryAllByRole('button', { name: /Remove/ })).toHaveLength(2);

    await user.click(screen.getAllByRole('button', { name: /Remove/ })[0]);
    detectChanges();
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();
    expect(screen.queryAllByText(/Required change \d+/)).toHaveLength(1);
  });

  function acceptedOption() {
    return screen.getByRole('radio', { name: /Accepted/ });
  }

  function amendsOption() {
    return screen.getByRole('radio', { name: /Operator changes required/ });
  }
});
