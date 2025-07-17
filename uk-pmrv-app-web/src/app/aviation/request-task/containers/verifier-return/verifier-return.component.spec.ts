import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { VERIFICATION_REPORT } from '@aviation/request-task/aer/ukets/aer-verify/tests/mock-verification-report';
import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import produce from 'immer';

import { TasksService } from 'pmrv-api';

import VerifierReturnComponent from './verifier-return.component';
import { aerVerifierReturnProvider } from './verifier-return-form.provider';

describe('VerifierReturnComponent', () => {
  let fixture: ComponentFixture<VerifierReturnComponent>;
  let page: Page;
  let store: RequestTaskStore;

  class Page extends BasePage<VerifierReturnComponent> {
    get changesRequiredValue() {
      return this.getInputValue('#changesRequired');
    }
    set changesRequiredValue(value: string) {
      this.setInputValue('#changesRequired', value);
    }

    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }
  const tasksService = mockClass(TasksService);
  const activatedRouteStub = new ActivatedRouteStub({ taskId: '237', index: '0' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierReturnComponent, SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        aerVerifierReturnProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestInfo: {
            type: 'AVIATION_AER_UKETS',
          },
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

    fixture = TestBed.createComponent(VerifierReturnComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Return to operator for changes');
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    page.changesRequiredValue = 'changes';

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
        changesRequired: 'changes',
      },
    });
  });
});
