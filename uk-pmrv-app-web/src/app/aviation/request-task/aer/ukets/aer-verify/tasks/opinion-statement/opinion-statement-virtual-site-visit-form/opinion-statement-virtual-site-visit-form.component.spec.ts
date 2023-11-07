import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementVirtualSiteVisitFormComponent from './opinion-statement-virtual-site-visit-form.component';

describe('OpinionStatementVirtualSiteVisitFormComponent', () => {
  let fixture: ComponentFixture<OpinionStatementVirtualSiteVisitFormComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementVirtualSiteVisitFormComponent, RouterTestingModule],
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

    TestBed.inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).addVirtualSiteGroup();

    fixture = TestBed.createComponent(OpinionStatementVirtualSiteVisitFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    fixture.detectChanges();

    expect(screen.getByText('Virtual site visit details')).toBeInTheDocument();
  });

  it('should display the form field', async () => {
    fixture.detectChanges();

    expect(screen.getByText(/Reasons for making a virtual visit/)).toBeInTheDocument();
  });
});
