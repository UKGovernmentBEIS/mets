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

import { AviationAerCorsiaEmissionsReductionClaim, TasksService } from 'pmrv-api';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { DeclarationOfNoDoubleClaimingComponent } from './declaration-of-no-double-claiming.component';

describe('DeclarationOfNoDoubleClaimingComponent', () => {
  let component: DeclarationOfNoDoubleClaimingComponent;
  let fixture: ComponentFixture<DeclarationOfNoDoubleClaimingComponent>;
  let store: RequestTaskStore;
  let formProvider: EmissionsReductionClaimFormProvider;

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
      imports: [DeclarationOfNoDoubleClaimingComponent, RouterTestingModule],
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
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
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

    formProvider = TestBed.inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataForm as AviationAerCorsiaEmissionsReductionClaim);

    fixture = TestBed.createComponent(DeclarationOfNoDoubleClaimingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveAer function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ emissionsReductionClaim: dataForm }, 'in progress');
  });
});
