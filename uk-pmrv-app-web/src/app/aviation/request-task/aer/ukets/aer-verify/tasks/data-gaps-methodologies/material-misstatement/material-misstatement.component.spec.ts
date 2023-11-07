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
import { DataGapsMethodologiesFormProvider } from '../data-gaps-methodologies-form.provider';
import { MaterialMisstatementComponent } from '../material-misstatement';
import { MethodologiesSummaryComponent } from '../methodologies-summary/methodologies-summary.component';

describe('MaterialMisstatementComponent', () => {
  let fixture: ComponentFixture<MaterialMisstatementComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let formProvider: DataGapsMethodologiesFormProvider;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MaterialMisstatementComponent,
        RouterTestingModule.withRoutes([
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

    fixture = TestBed.createComponent(MaterialMisstatementComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(screen.getByText('Did the method lead to a material misstatement?')).toBeInTheDocument();
  });

  it('should submit and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
  });
});
