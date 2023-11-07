import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { ConservativeMethodComponent } from '../conservative-method';
import { DataGapsMethodologiesFormProvider } from '../data-gaps-methodologies-form.provider';
import { MethodologiesSummaryComponent } from '../methodologies-summary/methodologies-summary.component';
import { RegulatorApprovedComponent } from './regulator-approved.component';

describe('RegulatorApprovedComponent', () => {
  let fixture: ComponentFixture<RegulatorApprovedComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let formProvider: DataGapsMethodologiesFormProvider;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RegulatorApprovedComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'method-conservative',
            component: ConservativeMethodComponent,
          },
          {
            path: 'summary',
            component: MethodologiesSummaryComponent,
          },
        ]),
      ],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsMethodologiesFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    formProvider = TestBed.inject<DataGapsMethodologiesFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(VERIFICATION_REPORT.dataGapsMethodologies);

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

    fixture = TestBed.createComponent(RegulatorApprovedComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(screen.getByText('Has the data gap method already been approved by the regulator?')).toBeInTheDocument();
  });

  it('should submit and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../method-conservative'], { relativeTo: activatedRoute });
  });
});
