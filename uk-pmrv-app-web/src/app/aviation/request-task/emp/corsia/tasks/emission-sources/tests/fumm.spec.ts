import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { AircraftTypeFormProvider } from '../aircraft-type/aircraft-type-form.provider';
import { EMP_EMISSION_SOURCES_ROUTES } from '../emission-sources.routes';
import { EmissionSourcesCorsiaFormProvider } from '../emission-sources-form.provider';
import { EmissionSourcesPageComponent } from '../emission-sources-page/emission-sources-page.component';
import {
  fillAircraftTypeFormWithFUMM,
  navigateToAddAircraftType,
  navigateToSearchAircractType,
  selectAircraftTypeFromSearch,
  SubmitAircraftTypeForm,
} from './helpers';

function setupStore(store: RequestTaskStore) {
  const state = store.getState();
  store.setState({
    ...state,
    isEditable: true,
    requestTaskItem: {
      ...state.requestTaskItem,
      requestInfo: {
        id: 'EMP_22',
        type: 'EMP_ISSUANCE_CORSIA',
      },
      requestTask: {
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
        payload: {
          operatorDetails: {
            operatorName: 'AviationOp13',
          },
          emissionsMonitoringPlan: {
            ...EmpCorsiaStoreDelegate.INITIAL_STATE,
            emissionsMonitoringApproach: { monitoringApproachType: 'FUEL_USE_MONITORING' },
          },
        } as any,
      },
    },
  });
}
describe('Emission sources integration - FUMM', () => {
  let store: RequestTaskStore;
  let harness: RouterTestingHarness;
  let httpTestingController: HttpTestingController;
  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        provideRouter(EMP_EMISSION_SOURCES_ROUTES),
        provideHttpClient(),
        provideHttpClientTesting(),
        EmissionSourcesCorsiaFormProvider,
        AircraftTypeFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
    await TestBed.compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/', EmissionSourcesPageComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('');
  });
  it('should render EmissionSourcesPageComponent', () => {
    expect(screen.getByTestId('emission-sources-page')).toBeInTheDocument();
    expect(screen.getByTestId('aicraft-types-table')).toBeInTheDocument();
    expect(screen.getByTestId('add-aicraft-btn')).toBeInTheDocument();
  });
  it('should navigate to Aicraft Type for on add button click', async () => {
    await userEvent.click(screen.getByTestId('add-aicraft-btn'));
    expect(TestBed.inject(Location).path()).toEqual('/aircraft-type/add?change=true');
    harness.detectChanges();
    expect(screen.getByTestId('aicraft-type-form')).toBeInTheDocument();
  });
  it('should select an aircraft type from the search form', async () => {
    await navigateToAddAircraftType(harness);
    await navigateToSearchAircractType(harness);
    await selectAircraftTypeFromSearch(harness, httpTestingController, 1);
  });
  it('should fill and submit aircraft type form', async () => {
    await navigateToAddAircraftType(harness);
    await navigateToSearchAircractType(harness);
    await selectAircraftTypeFromSearch(harness, httpTestingController, 2);
    await fillAircraftTypeFormWithFUMM(harness);
    await SubmitAircraftTypeForm(harness, httpTestingController);
  });
});
