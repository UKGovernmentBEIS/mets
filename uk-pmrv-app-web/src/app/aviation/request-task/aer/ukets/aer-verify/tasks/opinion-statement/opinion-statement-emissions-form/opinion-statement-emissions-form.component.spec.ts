import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { AerRequestTaskPayload, AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementEmissionsFormComponent from './opinion-statement-emissions-form.component';

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="emissionsGroup">
      <app-opinion-statement-emissions-form
        [totalEmissionsProvided]="totalEmissionsProvided"
      ></app-opinion-statement-emissions-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, OpinionStatementEmissionsFormComponent],
  providers: [
    { provide: TASK_FORM_PROVIDER, useClass: OpinionStatementFormProvider },
    { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
  ],
})
class MockParentComponent {
  emissionsGroup = inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).emissionsGroup;
  totalEmissionsProvided = '2802';
}

describe('OpinionStatementEmissionsFormComponent', () => {
  let fixture: ComponentFixture<MockParentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementEmissionsFormComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OpinionStatementFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              aer: AerStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
              verificationReport: {
                ...VERIFICATION_REPORT,
              },
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(MockParentComponent);
    fixture.detectChanges();
  });

  it('should create', async () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display all form fields', async () => {
    expect(screen.getByText(/Confirm the standard fuels and emission factors used/)).toBeInTheDocument();
    expect(screen.getByText(/Confirm the emissions monitoring approach used/)).toBeInTheDocument();
    expect(screen.getByText(/Total emissions reported by the operator/)).toBeInTheDocument();
    expect(screen.getByText(/Are the reported emissions correct?/)).toBeInTheDocument();
  });

  it('should not have selected option for "Are the reported emissions correct?"', async () => {
    expect(yesOption()).not.toBeChecked();
    expect(noOption()).not.toBeChecked();
  });

  function yesOption() {
    return screen.getByRole('radio', { name: /Yes, the emissions are correct/ });
  }

  function noOption() {
    return screen.getByRole('radio', { name: /No, we will enter a different verified emissions figure/ });
  }
});
