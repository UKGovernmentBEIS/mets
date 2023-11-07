import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { VerifyEmissionsReductionClaimFormProvider } from '../verify-emissions-reduction-claim-form.provider';
import VerifyEmissionsReductionClaimPageComponent from '../verify-emissions-reduction-claim-page/verify-emissions-reduction-claim-page.component';
import VerifyEmissionsReductionClaimSummaryComponent from './verify-emissions-reduction-claim-summary.component';

describe('VerifyEmissionsReductionClaimSummaryComponent', () => {
  let fixture: ComponentFixture<VerifyEmissionsReductionClaimSummaryComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyEmissionsReductionClaimPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VerifyEmissionsReductionClaimFormProvider },
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

    fixture = TestBed.createComponent(VerifyEmissionsReductionClaimSummaryComponent);
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

    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.emissionsReductionClaimVerification);

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute, replaceUrl: true });
  });
});
