import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import SendReportToOperatorConfirmationComponent from '../send-report-to-operator-confirmation/send-report-to-operator-confirmation.component';
import SendReportToOperatorPageComponent from './send-report-to-operator-page.component';

describe('SendReportToOperatorPageComponent', () => {
  let fixture: ComponentFixture<SendReportToOperatorPageComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SendReportToOperatorPageComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'confirmation-operator',
            component: SendReportToOperatorConfirmationComponent,
          },
        ]),
      ],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
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

    fixture = TestBed.createComponent(SendReportToOperatorPageComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should submit and navigate to confirmation-operator', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const submitAerVerifySpy = jest
      .spyOn(store.aerVerifyDelegate as AerVerifyStoreDelegate, 'submitAerVerify')
      .mockReturnValue(of({}));

    fixture.componentInstance.onSubmit();

    expect(submitAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['confirmation-operator'], {
      relativeTo: activatedRoute,
      replaceUrl: true,
    });
  });
});
