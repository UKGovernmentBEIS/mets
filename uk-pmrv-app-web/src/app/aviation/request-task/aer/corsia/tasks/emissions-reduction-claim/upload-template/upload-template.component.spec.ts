import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { AviationAerCorsiaEmissionsReductionClaim, TasksService } from 'pmrv-api';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { UploadTemplateComponent } from './upload-template.component';

describe('UploadTemplateComponent', () => {
  let component: UploadTemplateComponent;
  let fixture: ComponentFixture<UploadTemplateComponent>;
  let store: RequestTaskStore;
  let formProvider: EmissionsReductionClaimFormProvider;
  let user: UserEvent;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const data = {
    exist: true,
    emissionsReductionClaimDetails: {
      cefFiles: [
        {
          file: {
            name: 'New file',
          },
          uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
        },
      ],
      totalEmissions: '123',
      noDoubleCountingDeclarationFiles: [
        {
          file: {
            name: 'New file',
          },
          uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
        },
      ],
    },
  };

  const dataForm = {
    ...data,
    emissionsReductionClaimDetails: {
      ...data.emissionsReductionClaimDetails,
      cefFiles: data.emissionsReductionClaimDetails.cefFiles.map((f) => f.uuid),
      noDoubleCountingDeclarationFiles: data.emissionsReductionClaimDetails.noDoubleCountingDeclarationFiles.map(
        (f) => f.uuid,
      ),
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadTemplateComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      isEditable: true,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 555,
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            ...AerCorsiaStoreDelegate.INITIAL_STATE,
            emissionsReductionClaim: { exist: true, emissionsReductionClaimDetails: {} },
            reviewSectionsCompleted: {},
            aerAttachments: {
              'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22': 'New file',
            },
          } as any,
        },
      },
    });

    user = userEvent.setup();
    formProvider = TestBed.inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue({
      exist: true,
      emissionsReductionClaimDetails: {},
    } as AviationAerCorsiaEmissionsReductionClaim);
    fixture = TestBed.createComponent(UploadTemplateComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter the total emissions reduction claimed/)).toHaveLength(3);
  });

  it('should call the saveAer function with the correct data when the form is valid', async () => {
    const state = store.getState();

    store.setState({
      ...state,
      isEditable: true,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 555,
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            ...AerCorsiaStoreDelegate.INITIAL_STATE,
            emissionsReductionClaim: { ...dataForm },

            reviewSectionsCompleted: {},
            aerAttachments: {
              'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22': 'New file',
            },
          } as any,
        },
      },
    });
    formProvider.setFormValue(dataForm as AviationAerCorsiaEmissionsReductionClaim);
    fixture.detectChanges();
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ emissionsReductionClaim: dataForm }, 'in progress');
  });
});
