import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { mockClass } from '@testing';
import { fireEvent } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { AviationAerSaf, TasksService } from 'pmrv-api';

import { AerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimRemoveBatchItemComponent } from './emissions-reduction-claim-remove-batch-item.component';

describe('EmissionsReductionClaimRemoveBatchItemComponent', () => {
  let component: EmissionsReductionClaimRemoveBatchItemComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimRemoveBatchItemComponent>;
  let store: RequestTaskStore;

  let formProvider: AerEmissionsReductionClaimFormProvider;

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
      imports: [EmissionsReductionClaimRemoveBatchItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AerEmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: new Map([['batchIndex', 1]]),
            },
          },
        },
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

    fixture = TestBed.createComponent(EmissionsReductionClaimRemoveBatchItemComponent);
    component = fixture.componentInstance;
    formProvider = TestBed.inject<AerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataForm as AviationAerSaf);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have appropriate title header', () => {
    expect(screen.getByText('Are you sure you want to delete this item?')).toBeInTheDocument();
  });
  it('should have appropriate remove button', () => {
    expect(getRemoveBtn()).toBeInTheDocument();
  });

  it('should remove second batch item', () => {
    expect(formProvider.form.getRawValue().safDetails.purchases.length).toEqual(2);
    fireEvent.click(getRemoveBtn());
    getRemoveBtn().click();
    fixture.detectChanges();
    expect(formProvider.form.getRawValue().safDetails.purchases.length).toEqual(1);
  });
  function getRemoveBtn(): HTMLElement {
    return fixture.debugElement.nativeElement.querySelector('button');
  }
});
