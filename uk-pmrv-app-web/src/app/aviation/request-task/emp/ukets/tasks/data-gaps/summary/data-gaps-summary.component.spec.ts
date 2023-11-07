import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskPageComponent } from '@aviation/request-task/containers';
import { EMP_CHILD_ROUTES } from '@aviation/request-task/emp/ukets/emp.routes';
import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/dom';

import { dataGapsFormProvider } from '../data-gaps-form.provider';
import { DataGapsSummaryComponent } from './data-gaps-summary.component';

const dataGaps = {
  dataGaps: 'data gap',
  secondaryDataSources: 'secondary data source',
  substituteData: 'substitute datra',
  otherDataGapsTypes: 'other data gap types',
};

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
              otherFuelExplanation: 'Other fuel explanation',
              multipleFuelConsumptionMethodsExplanation: ' multiple Fuel Consumption',
              additionalAircraftMonitoringApproach: {
                procedureDescription: 'procedure desc',
                procedureDocumentName: 'dc name',
                procedureReference: 'p reference',
                responsibleDepartmentOrRole: 'responsibility or role',
                locationOfRecords: 'location',
                itSystemUsed: 'it system usedl',
              },
            },
            emissionsMonitoringApproach: {
              monitoringApproachType: 'FUEL_USE_MONITORING',
            },
            dataGaps,
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
  let store: RequestTaskStore;
  let harness: RouterTestingHarness;

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
    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/data-gaps', DataGapsSummaryComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/data-gaps/summary');
  });
  it('should render data gaps summary form', () => {
    harness.detectChanges();
    expect(screen.getByTestId('data-gaps-summary')).toBeInTheDocument();
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    for (const key of Object.keys(dataGaps)) {
      expect(screen.getByText(dataGaps[key])).toBeInTheDocument();
    }
  });
});
