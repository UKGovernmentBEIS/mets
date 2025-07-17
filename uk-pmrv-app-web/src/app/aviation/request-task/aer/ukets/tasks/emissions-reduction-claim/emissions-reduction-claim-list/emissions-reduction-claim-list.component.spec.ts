import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';

import { AviationAerSaf } from 'pmrv-api';

import { AerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimListComponent } from './emissions-reduction-claim-list.component';

describe('EmissionsReductionClaimListTemplateComponent', () => {
  let component: EmissionsReductionClaimListComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimListComponent>;
  let formProvider: AerEmissionsReductionClaimFormProvider;
  let store: RequestTaskStore;

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
        {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AerEmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
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
            ...AerUkEtsStoreDelegate.INITIAL_STATE,
            saf: { ...dataForm },

            reviewSectionsCompleted: {},
            aerAttachments: {
              'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22': 'New file',
            },
          } as any,
        },
      },
    });

    formProvider = TestBed.inject<AerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataForm as AviationAerSaf);
    fixture = TestBed.createComponent(EmissionsReductionClaimListComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
