import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { AircraftTypeFormProvider } from '../aircraft-type/aircraft-type-form.provider';
import { EMP_EMISSION_SOURCES_ROUTES } from '../emission-sources.routes';
import { EmissionSourcesFormProvider } from '../emission-sources-form.provider';
import { EmissionSourcesPageComponent } from '../emission-sources-page/emission-sources-page.component';
import {
  fillAircraftTypeForm,
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
      requestTask: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        payload: {
          operatorDetails: {
            operatorName: 'AviationOp13',
            crcoCode: '1231412',
          },
          emissionsMonitoringPlan: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
          },
          empSectionsCompleted: {},
        } as any,
      },
    },
  });
}
describe('Emission sources integration - Simplified Approach', () => {
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
        EmissionSourcesFormProvider,
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
    expect(screen.getByTestId('aicraft-types-in-use-table')).toBeInTheDocument();
    expect(screen.getByTestId('aicraft-types-not-in-use-table')).toBeInTheDocument();
    expect(screen.getByTestId('add-aicraft-in-use-btn')).toBeInTheDocument();
    expect(screen.getByTestId('add-aicraft-not-in-use-btn')).toBeInTheDocument();
  });
  it('should navigate to Aicraft Type for on add button click', async () => {
    await userEvent.click(screen.getByTestId('add-aicraft-in-use-btn'));
    expect(TestBed.inject(Location).path()).toEqual('/aircraft-type/add?isCurrentlyUsed=1&change=true');
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
    await fillAircraftTypeForm(harness);
    await SubmitAircraftTypeForm(harness, httpTestingController);
  });
  it('should navigate to other form if any other fuel was selected', async () => {
    await navigateToAddAircraftType(harness);
    await navigateToSearchAircractType(harness);
    await selectAircraftTypeFromSearch(harness, httpTestingController, 2);
    await fillAircraftTypeForm(harness);
    await SubmitAircraftTypeForm(harness, httpTestingController);
    harness.detectChanges();
    await userEvent.click(screen.getByText('Continue'));
    harness.detectChanges();
    expect(await screen.findByTestId('aircraft-other-fuel-form')).toBeInTheDocument();
  });
});
