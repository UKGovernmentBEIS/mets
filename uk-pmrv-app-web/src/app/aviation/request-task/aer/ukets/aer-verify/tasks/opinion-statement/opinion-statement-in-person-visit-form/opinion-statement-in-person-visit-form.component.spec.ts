import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementInPersonVisitFormComponent from './opinion-statement-in-person-visit-form.component';

describe('OpinionStatementInPersonVisitFormComponent', () => {
  let fixture: ComponentFixture<OpinionStatementInPersonVisitFormComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementInPersonVisitFormComponent, RouterTestingModule],
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

    TestBed.inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).addInPersonSiteGroup();

    fixture = TestBed.createComponent(OpinionStatementInPersonVisitFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    fixture.detectChanges();

    expect(screen.getByText('In-person site visit details')).toBeInTheDocument();
  });

  it('should display all form fields', async () => {
    fixture.detectChanges();

    expect(screen.getByText(/When did the site visit begin?/)).toBeInTheDocument();
    expect(screen.getByText(/How many days were your team on site?/)).toBeInTheDocument();
    expect(screen.getByText(/Which team members made the site visit?/)).toBeInTheDocument();
  });
});
