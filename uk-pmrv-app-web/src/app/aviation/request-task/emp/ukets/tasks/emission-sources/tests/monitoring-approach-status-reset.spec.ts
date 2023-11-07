import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskPageComponent } from '@aviation/request-task/containers';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/emp/shared/util/emp.util';
import { EMP_CHILD_ROUTES } from '@aviation/request-task/emp/ukets/emp.routes';
import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen, waitForElementToBeRemoved } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/setup/setup';

import { MonitoringApproachFormProvider, MonitoringSummaryComponent } from '../../monitoring-approach';
import { basePath } from './helpers';
import { saveEmpFixtureWithFumm } from './save-emp.fixture';

function setupStore(store: RequestTaskStore) {
  const state = store.getState();
  store.setState({
    ...state,
    isEditable: true,
    requestTaskItem: {
      ...state.requestTaskItem,
      requestTask: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        payload: {
          emissionsMonitoringPlan: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            emissionSources: {
              aircraftTypes: [
                {
                  aircraftTypeInfo: {
                    manufacturer: '(any manufacturer)',
                    model: 'Airship',
                    designatorType: 'SHIP',
                  },
                  fuelTypes: ['JET_KEROSENE', 'OTHER'],
                  isCurrentlyUsed: true,
                  numberOfAircrafts: 12,
                  subtype: 'sdfasasdasd',
                },
                {
                  aircraftTypeInfo: {
                    manufacturer: '(any manufacturer)',
                    model: 'Microlight aircraft',
                    designatorType: 'ULAC',
                  },
                  fuelTypes: ['JET_KEROSENE'],
                  isCurrentlyUsed: false,
                  numberOfAircrafts: 12,
                  subtype: 'zxcdsdf',
                },
              ],
              otherFuelExplanation: 'fghf',
            },
            emissionsMonitoringApproach: {
              monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
              explanation: 'some explanation',
            },
          },
          empSectionsCompleted: {
            emissionSources: [true],
            emissionsMonitoringApproach: [true],
          },
        } as any,
      },
    },
  });
}
describe('Emission sources - Monitoring Approach integration test', () => {
  let store: RequestTaskStore;
  let harness: RouterTestingHarness;
  let httpTestingController: HttpTestingController;
  let user: UserEvent;
  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [RequestTaskPageComponent],
      imports: [ReactiveFormsModule],
      providers: [
        provideRouter(EMP_CHILD_ROUTES),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
    await TestBed.compileComponents();
    user = userEvent.setup();
    httpTestingController = TestBed.inject(HttpTestingController);
    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/monitoring-approach', MonitoringSummaryComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/monitoring-approach/summary');
  });
  it('should change emission sources completion status when monitoring approach changes from FUMM to SA', async () => {
    await user.click(screen.getAllByText('Change')[0]);
    harness.detectChanges();
    let req;
    await user.click(document.getElementById('monitoringApproachType-option2'));
    await user.click(screen.getByText('Continue'));
    harness.detectChanges();
    req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
    expect(req.request.method).toEqual('POST');
    req.flush(saveEmpFixtureWithFumm);
    await waitForElementToBeRemoved(document.getElementById('monitoringApproachType-option2'));
    harness.detectChanges();
    expect(TestBed.inject(Location).path()).toEqual('/monitoring-approach/summary');
    await user.click(screen.getByText('Confirm and complete'));
    req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
    expect(req.request.method).toEqual('POST');
    req.flush(saveEmpFixtureWithFumm);
    httpTestingController.verify();
    harness.detectChanges();
    const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts;
    // falling back to store value testing because it was too much to bootstrap all of RequestTaskPageComponent to get statuses by css
    // Ideally we should only test via user interaction
    expect(isFUMM(payload)).toBeTruthy();
    expect(payload.empSectionsCompleted.emissionSources[0]).toBeFalsy();
    const emissionSourcesStatus = getTaskStatusByTaskCompletionState('emissionSources', payload);
    expect(emissionSourcesStatus).toEqual('needs review');
  });
});
