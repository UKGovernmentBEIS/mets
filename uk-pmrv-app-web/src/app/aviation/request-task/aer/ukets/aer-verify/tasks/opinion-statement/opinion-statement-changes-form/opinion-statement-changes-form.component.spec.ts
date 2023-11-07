import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import OpinionStatementVisitFormComponent from '../opinion-statement-visit-form/opinion-statement-visit-form.component';
import OpinionStatementChangesFormComponent from './opinion-statement-changes-form.component';

describe('OpinionStatementChangesFormComponent', () => {
  let fixture: ComponentFixture<OpinionStatementChangesFormComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        OpinionStatementChangesFormComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'site-visit',
            component: OpinionStatementVisitFormComponent,
          },
        ]),
      ],
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
              aer: AerStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
              verificationReport: {
                ...VERIFICATION_REPORT,
              },
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(OpinionStatementChangesFormComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(
      screen.getByText('Other changes not covered in the approved emissions monitoring plans'),
    ).toBeInTheDocument();
  });

  it('should give the user the option to select if there were any changes agreed with the regulator that were not covered in the approved emissions monitoring plans', async () => {
    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  it('should not have selected option', async () => {
    expect(yesOption()).not.toBeChecked();
    expect(noOption()).not.toBeChecked();
  });

  it('should submit and navigate to site-visit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.opinionStatement);

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['..', 'site-visit'], { relativeTo: activatedRoute });
  });

  function yesOption() {
    return screen.getByRole('radio', { name: /Yes/ });
  }

  function noOption() {
    return screen.getByRole('radio', { name: /No/ });
  }
});
