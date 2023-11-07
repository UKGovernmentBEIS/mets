import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { AviationAerSaf, TasksService } from 'pmrv-api';

import { aerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimAddBatchItemComponent } from './emissions-reduction-claim-add-batch-item.component';

describe('EmissionsReductionClaimAddBatchItemComponent', () => {
  let component: EmissionsReductionClaimAddBatchItemComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimAddBatchItemComponent>;
  let store: RequestTaskStore;
  let formProvider: aerEmissionsReductionClaimFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const data = {
    exist: true,
    safDetails: {
      purchases: [
        {
          batchNumber: '1',
          fuelName: 'A',
          safMass: '33',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: [
            {
              file: { name: 'New file' } as File,
              uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
            },
          ],
          otherSustainabilityCriteriaEvidenceDescription: null,
        },
      ],
      noDoubleCountingDeclarationFile: {
        file: { name: 'New file' } as File,
        uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
      },
      totalSafMass: '35',
      emissionsFactor: '3.15',
      totalEmissionsReductionClaim: '110.25',
    },
  };

  const dataForm = {
    ...data,
    safDetails: {
      ...data.safDetails,
      noDoubleCountingDeclarationFile: data.safDetails.noDoubleCountingDeclarationFile.uuid,
    },
  } as unknown as AviationAerSaf;
  dataForm.safDetails?.purchases.forEach((purchase, index) => {
    if (purchase.evidenceFiles) {
      dataForm.safDetails.purchases[index].evidenceFiles = purchase.evidenceFiles.map((fu) => fu['uuid']);
    }
  });

  const dataExpected = {
    ...dataForm,
    safDetails: {
      ...dataForm.safDetails,
      purchases: [
        ...dataForm.safDetails.purchases,
        {
          batchNumber: '2',
          fuelName: 'B',
          safMass: '2',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: ['bb94a5ec-3a94-4472-9dc5-f1c51fef3d22'],
        },
      ],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimAddBatchItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: aerEmissionsReductionClaimFormProvider },
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
        requestTask: {
          type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
          payload: {
            ...AerStoreDelegate.INITIAL_STATE,
            saf: { ...dataForm },

            reviewSectionsCompleted: {},
            aerAttachments: {
              'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22': 'New file',
            },
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(EmissionsReductionClaimAddBatchItemComponent);
    component = fixture.componentInstance;
    formProvider = TestBed.inject<aerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataForm as AviationAerSaf);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveAer function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.form.setValue({
      batchNumber: '2',
      fuelName: 'B',
      safMass: '2',
      sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
      evidenceFiles: [
        {
          file: { name: 'New file' } as File,
          uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
        },
      ],
      otherSustainabilityCriteriaEvidenceDescription: null,
    });
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ saf: dataExpected }, 'in progress');
  });
});
