import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskPageComponent } from '@aviation/request-task/containers';
import { EMP_CHILD_ROUTES } from '@aviation/request-task/emp/ukets/emp.routes';
import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen, waitForElementToBeRemoved } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/setup/setup';

import { dataGapsFormProvider } from '../data-gaps-form.provider';
import { createDataGapsFixture } from '../tests/fixture';
import { DataGapsFormComponent } from './data-gaps-form.component';

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
                  fuelConsumptionMeasuringMethod: 'BLOCK_HOUR',
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
                  fuelConsumptionMeasuringMethod: 'METHOD_A',
                },
              ],
              otherFuelExplanation: 'fghf',
              multipleFuelConsumptionMethodsExplanation: 'hjghj',
              additionalAircraftMonitoringApproach: {
                procedureDescription: 'kjhkjhkj',
                procedureDocumentName: 'hkj',
                procedureReference: 'hkj',
                responsibleDepartmentOrRole: 'hkj',
                locationOfRecords: 'hkj',
                itSystemUsed: 'hk',
              },
            },
            emissionsMonitoringApproach: {
              monitoringApproachType: 'FUEL_USE_MONITORING',
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
describe('Data Gaps Form', () => {
  const basePath = 'http://localhost:8080/api';
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
        dataGapsFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
    await TestBed.compileComponents();
    user = userEvent.setup();
    httpTestingController = TestBed.inject(HttpTestingController);
    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/data-gaps', DataGapsFormComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/data-gaps');
  });
  it('should render data gaps form', () => {
    expect(screen.getByTestId('data-gaps')).toBeInTheDocument();
    expect(screen.getByText('Data gaps when monitoring fuel consumption')).toBeInTheDocument();
    expect(document.getElementById('dataGaps')).toBeTruthy();
  });
  it('should populate form', async () => {
    const input = 'Data gaps desc';
    const textarea = document.getElementById('dataGaps');
    await user.type(textarea, input);
    harness.detectChanges();
    await user.click(screen.getByText('Continue'));
    const req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
    expect(req.request.method).toEqual('POST');
    req.flush(createDataGapsFixture({ dataGaps: input }));
    harness.detectChanges();
    await waitForElementToBeRemoved(textarea);
    expect(await screen.findByTestId('secondary-data-sources')).toBeInTheDocument();
  });
});
