import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementSummaryComponent from './opinion-statement-summary.component';

describe('OpinionStatementSummaryComponent', () => {
  let fixture: ComponentFixture<OpinionStatementSummaryComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementSummaryComponent, RouterTestingModule],
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
              aer: AerUkEtsStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
              verificationReport: {
                ...VERIFICATION_REPORT,
              },
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(OpinionStatementSummaryComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.opinionStatement);

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute, replaceUrl: true });
  });
});
