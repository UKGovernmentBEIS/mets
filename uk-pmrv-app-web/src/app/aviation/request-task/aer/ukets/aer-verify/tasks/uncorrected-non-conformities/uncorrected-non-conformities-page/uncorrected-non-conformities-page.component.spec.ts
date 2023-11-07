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
import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';
import UncorrectedNonConformitiesPriorYearIssuesComponent from '../uncorrected-non-conformities-prior-year-issues/uncorrected-non-conformities-prior-year-issues.component';
import UncorrectedNonConformitiesPageComponent from './uncorrected-non-conformities-page.component';

describe('UncorrectedNonConformitiesPageComponent', () => {
  let fixture: ComponentFixture<UncorrectedNonConformitiesPageComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        UncorrectedNonConformitiesPageComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'prior-year-issues',
            component: UncorrectedNonConformitiesPriorYearIssuesComponent,
          },
        ]),
      ],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonConformitiesFormProvider },
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

    fixture = TestBed.createComponent(UncorrectedNonConformitiesPageComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(
      screen.getByText('Have there been any uncorrected non-conformities with the approved emissions monitoring plan?'),
    ).toBeInTheDocument();
  });

  it('should submit and navigate to prior-year-issues', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.uncorrectedNonConformities);

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['prior-year-issues'], { relativeTo: activatedRoute });
  });
});
