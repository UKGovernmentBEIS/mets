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
import { MaterialityLevelFormProvider } from '../materiality-level-form.provider';
import MaterialityLevelPageComponent from './materiality-level-page.component';

describe('MaterialityLevelPageComponent', () => {
  let fixture: ComponentFixture<MaterialityLevelPageComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MaterialityLevelPageComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'reference-documents',
            component: MaterialityLevelPageComponent,
          },
        ]),
      ],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MaterialityLevelFormProvider },
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

    fixture = TestBed.createComponent(MaterialityLevelPageComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(screen.getByText('Materiality level')).toBeInTheDocument();
  });

  it('should submit and navigate to reference-documents', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.materialityLevel);

    fixture.componentInstance.onSubmit();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['reference-documents'], { relativeTo: activatedRoute });
  });
});
