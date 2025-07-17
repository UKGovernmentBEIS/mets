import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestActionPageComponent } from '@aviation/request-action/containers';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/dom';

import { EMP_ROUTES } from '../../../ukets/emp.routes';
import { DataGapsComponent } from './data-gaps.component';

const dataGaps = {
  dataGaps: 'data gap',
  secondaryDataSources: 'secondary data source',
  substituteData: 'substitute datra',
  otherDataGapsTypes: 'other data gap types',
};

function setupStore(store: RequestActionStore) {
  const state = store.getState();
  store.setState({
    ...state,
    requestActionItem: {
      type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
      creationDate: '2022-11-29T12:12:48.469862Z',
      payload: {
        emissionsMonitoringPlan: {
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
  });
}
describe('Data Gaps Form', () => {
  let store: RequestActionStore;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [RequestActionPageComponent],
      providers: [
        provideRouter(EMP_ROUTES),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    });
    await TestBed.compileComponents();
    store = TestBed.inject(RequestActionStore);
    setupStore(store);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/submitted/data-gaps', DataGapsComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/submitted/data-gaps');
  });
  it('should render data gaps summary form', () => {
    harness.detectChanges();
    expect(screen.getByTestId('data-gaps-summary-template')).toBeInTheDocument();
    for (const key of Object.keys(dataGaps)) {
      expect(screen.getByText(dataGaps[key])).toBeInTheDocument();
    }
  });
});
