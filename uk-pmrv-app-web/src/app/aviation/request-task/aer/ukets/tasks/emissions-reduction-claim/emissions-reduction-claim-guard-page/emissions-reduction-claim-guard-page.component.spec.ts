import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/dom';

import { AviationAerSaf, TasksService } from 'pmrv-api';

import { AerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimGuardPageComponent } from './emissions-reduction-claim-guard-page.component';

describe('EmissionsReductionClaimGuardPageComponent', () => {
  let component: EmissionsReductionClaimGuardPageComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimGuardPageComponent>;
  let store: RequestTaskStore;
  let formProvider: AerEmissionsReductionClaimFormProvider;

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
      imports: [EmissionsReductionClaimGuardPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AerEmissionsReductionClaimFormProvider },
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

    fixture = TestBed.createComponent(EmissionsReductionClaimGuardPageComponent);
    component = fixture.componentInstance;
    formProvider = TestBed.inject<AerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataForm as AviationAerSaf);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should give the user the option to select if they want to provide emissions reduction claim or not', async () => {
    expect(
      screen.getByText(
        'Will you be making an emissions reduction claim as a result of the purchase and delivery of SAF?',
      ),
    ).toBeTruthy();
    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  function yesOption() {
    return screen.getByRole('radio', { name: /Yes/ });
  }

  function noOption() {
    return screen.getByRole('radio', { name: /No/ });
  }

  it('should call the saveAer function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.form.get('exist').patchValue(true);
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ saf: dataForm }, 'in progress');
  });
});
