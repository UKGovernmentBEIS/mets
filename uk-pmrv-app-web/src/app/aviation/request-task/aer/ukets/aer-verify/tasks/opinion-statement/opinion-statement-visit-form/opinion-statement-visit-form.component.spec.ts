import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementVisitFormComponent from './opinion-statement-visit-form.component';

describe('OpinionStatementVisitFormComponent', () => {
  let fixture: ComponentFixture<OpinionStatementVisitFormComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementVisitFormComponent, RouterTestingModule],
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
              verificationReport: {
                ...VERIFICATION_REPORT,
              },
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );

    TestBed.inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).form;

    fixture = TestBed.createComponent(OpinionStatementVisitFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    fixture.detectChanges();

    expect(screen.getByText('What kind of site visit did your team make?')).toBeInTheDocument();
  });

  it('should give the user the option to select the kind of site visit the team made', async () => {
    fixture.detectChanges();

    expect(inPersonOption()).toBeInTheDocument();
    expect(virtualOption()).toBeInTheDocument();
  });

  it('should not have selected option', async () => {
    fixture.detectChanges();

    expect(inPersonOption()).not.toBeChecked();
    expect(virtualOption()).not.toBeChecked();
  });

  function inPersonOption() {
    return screen.getByRole('radio', { name: /In-person site visit/ });
  }

  function virtualOption() {
    return screen.getByRole('radio', { name: /Virtual site visit/ });
  }
});
